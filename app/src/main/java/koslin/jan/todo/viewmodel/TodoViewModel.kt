package koslin.jan.todo.viewmodel

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import koslin.jan.todo.App
import koslin.jan.todo.Notification
import koslin.jan.todo.Notification.Companion.EXTRA_TODO
import koslin.jan.todo.entity.Todo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TodoViewModel(private val application: Application) : AndroidViewModel(application) {
    private val todoDao = App.database.todoDao()
    var todoList: LiveData<List<Todo>> = todoDao.getAllTodosLiveData()

    fun deleteTodo(todo: Todo) {
        viewModelScope.launch(Dispatchers.IO) {
            todoDao.delete(todo)
            todoList = todoDao.getAllTodosLiveData();
        }
    }

    fun addTodo(todo: Todo) {
        viewModelScope.launch(Dispatchers.IO) {
            val todoId = todoDao.insert(todo)
            val insertedTodo = todoDao.getTodoById(todoId)!!
            todoList = todoDao.getAllTodosLiveData()

            val alarmManager = application.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(application, Notification::class.java).apply {
                putExtra(EXTRA_TODO, Gson().toJson(insertedTodo))
            }
            val pendingIntent = PendingIntent.getBroadcast(
                application,
                todoId.toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val dueDate = insertedTodo.dueDate
            val notificationTime = dueDate - 10 * 60 * 1000 // 10 minutes before due date
            Log.d("ALARM", "OBLICZONY")
            if (alarmManager.canScheduleExactAlarms()) {
                Log.d("ALARM", "USTAWIONY")
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, notificationTime, pendingIntent)
            }
        }
    }

    fun toggleTodoState(todo: Todo) {
        viewModelScope.launch(Dispatchers.IO) {
            if (todo.status === Todo.Status.ACTIVE) {
                todoDao.setCompleted(todo.id)
            } else {
                todoDao.setActive(todo.id)
            }
            todoList = todoDao.getAllTodosLiveData();
        }
    }
}
