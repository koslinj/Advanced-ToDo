package koslin.jan.todo.viewmodel

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import koslin.jan.todo.App
import koslin.jan.todo.Notification
import koslin.jan.todo.Notification.Companion.EXTRA_TODO
import koslin.jan.todo.config.Keys
import koslin.jan.todo.entity.Todo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TodoViewModel(private val application: Application) : AndroidViewModel(application) {
    private val todoDao = App.database.todoDao()
    var todoList: MutableLiveData<List<Todo>> = MutableLiveData()
    private val defaultPreferences = PreferenceManager.getDefaultSharedPreferences(application)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val showActiveTodos = defaultPreferences.getBoolean(Keys.VISIBILITY_KEY, false)

            // Update todoList based on the value
            if (showActiveTodos) {
                todoList.postValue(todoDao.getActiveTodos())
            } else {
                todoList.postValue(todoDao.getAllTodos())
            }
        }
    }

    fun deleteTodo(todo: Todo) {
        viewModelScope.launch(Dispatchers.IO) {
            todoDao.delete(todo)
            todoList.postValue(todoDao.getAllTodos())
        }
    }

    fun addTodo(todo: Todo) {
        viewModelScope.launch(Dispatchers.IO) {
            val todoId = todoDao.insert(todo)
            val insertedTodo = todoDao.getTodoById(todoId)!!
            todoList.postValue(todoDao.getAllTodos())

            scheduleReminder(insertedTodo)
        }
    }

    fun toggleTodoState(todo: Todo) {
        viewModelScope.launch(Dispatchers.IO) {
            if (todo.status === Todo.Status.ACTIVE) {
                todoDao.setCompleted(todo.id)
            } else {
                todoDao.setActive(todo.id)
            }
            todoList.postValue(todoDao.getAllTodos())
        }
    }

    // Function to switch between showing active and all todos
    fun showActiveOnly(bool: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            if (bool) {
                // Show only active todos
                todoList.postValue(todoDao.getActiveTodos())
            } else {
                // Show all todos
                todoList.postValue(todoDao.getAllTodos())
            }
        }

    }

    private fun scheduleReminder(insertedTodo: Todo) {
        val alarmManager = application.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(application, Notification::class.java).apply {
            putExtra(EXTRA_TODO, Gson().toJson(insertedTodo))
        }
        val pendingIntent = PendingIntent.getBroadcast(
            application,
            insertedTodo.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Cancel any existing alarms with the same pending intent
        // chwilowo chyba nie potrzebne ale do update sie moze przydac
        alarmManager.cancel(pendingIntent)

        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(application)
        val notificationInterval = sharedPrefs.getString(Keys.BEFORE_KEY, "600")?.toIntOrNull() ?: 600

        val dueDate = insertedTodo.dueDate
        val notificationTime = dueDate - notificationInterval * 1000
        Log.d("ALARM", "OBLICZONY")
        if (alarmManager.canScheduleExactAlarms()) {
            Log.d("ALARM", "USTAWIONY")
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, notificationTime, pendingIntent)
        }
    }
}
