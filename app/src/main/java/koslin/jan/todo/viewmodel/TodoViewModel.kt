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
import koslin.jan.todo.entity.Attachment
import koslin.jan.todo.entity.Todo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TodoViewModel(private val application: Application) : AndroidViewModel(application) {
    private val todoDao = App.database.todoDao()
    var todoList: MutableLiveData<List<Todo>> = MutableLiveData()
    private val defaultPreferences = PreferenceManager.getDefaultSharedPreferences(application)
    private var showActiveTodos = defaultPreferences.getBoolean(Keys.VISIBILITY_KEY, false)
    private var categoriesToShow = defaultPreferences.getStringSet(Keys.CATEGORIES_KEY, setOf("work", "family", "sport"))

    init {
        viewModelScope.launch(Dispatchers.IO) {
            // Update todoList based on the value
            refreshVisibleTodos()
        }
    }

    fun searchTodosByTitle(titleQuery: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val todos = todoDao.searchTodosByTitle("%$titleQuery%")
            todoList.postValue(todos)
        }
    }


    fun deleteTodo(todo: Todo) {
        viewModelScope.launch(Dispatchers.IO) {
            todoDao.delete(todo)

            refreshVisibleTodos()
        }
    }

    fun addTodoWithAttachments(todo: Todo, attachments: List<Attachment>) {
        viewModelScope.launch(Dispatchers.IO) {
            val todoId = todoDao.insert(todo)
            attachments.forEach { attachment ->
                attachment.todoId = todoId
                todoDao.insertAttachment(attachment)
            }
            val insertedTodo = todoDao.getTodoById(todoId)!!

            refreshVisibleTodos()

            scheduleReminder(insertedTodo)
        }
    }

    fun updateTodoWithAttachments(
        todo: Todo,
        attachments: List<Attachment>,
        callback: (
            todo: Todo,
            attachments: List<Attachment>
        ) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            todoDao.update(todo)

            todoDao.deleteAttachmentsByTodoId(todo.id)
            attachments.forEach { attachment ->
                attachment.todoId = todo.id
                todoDao.insertAttachment(attachment)
            }
            val updatedTodo = todoDao.getTodoById(todo.id)!!

            refreshVisibleTodos()

            scheduleReminder(updatedTodo)

            withContext(Dispatchers.Main) {
                // Notify the callback with the updated data
                callback(updatedTodo, attachments)
            }
        }
    }


    fun addTodo(todo: Todo) {
        viewModelScope.launch(Dispatchers.IO) {
            val todoId = todoDao.insert(todo)
            val insertedTodo = todoDao.getTodoById(todoId)!!

            refreshVisibleTodos()

            scheduleReminder(insertedTodo)
        }
    }

    fun updateTodo(todo: Todo) {
        viewModelScope.launch(Dispatchers.IO) {
            todoDao.update(todo)
            val updatedTodo = todoDao.getTodoById(todo.id)!!

            refreshVisibleTodos()

            scheduleReminder(updatedTodo)
        }
    }

    fun toggleTodoState(todo: Todo) {
        viewModelScope.launch(Dispatchers.IO) {
            if (todo.status === Todo.Status.ACTIVE) {
                todoDao.setCompleted(todo.id)
            } else {
                todoDao.setActive(todo.id)
            }

            refreshVisibleTodos()
        }
    }

    fun turnOnTodoNotifications(todo: Todo) {
        viewModelScope.launch(Dispatchers.IO) {
            todoDao.turnOnNotifications(todo.id)
            scheduleReminder(todo)

            refreshVisibleTodos()
        }
    }

    fun turnOffTodoNotifications(todo: Todo) {
        viewModelScope.launch(Dispatchers.IO) {
            todoDao.turnOffNotifications(todo.id)
            cancelNotificationAlarm(todo)

            refreshVisibleTodos()
        }
    }

    // Function to switch between showing active and all todos
    fun showActiveOnly(bool: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            showActiveTodos = bool
            refreshVisibleTodos()
        }
    }

    fun fetchTodosByCategories(selectedCategories: HashSet<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            categoriesToShow = selectedCategories
            refreshVisibleTodos()
        }
    }

    fun publicRefresh() {
        viewModelScope.launch(Dispatchers.IO){
            refreshVisibleTodos()
        }
    }


    private suspend fun refreshVisibleTodos() {
        if (showActiveTodos) {
            todoList.postValue(todoDao.getTodosByCategoriesAndStatus(categoriesToShow!!, "ACTIVE"))
        } else {
            todoList.postValue(todoDao.getTodosByCategories(categoriesToShow!!))
        }
    }

    private fun cancelNotificationAlarm(todo: Todo) {
        val alarmManager = application.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(application, Notification::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            application,
            todo.id.toInt(),
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
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
        alarmManager.cancel(pendingIntent)

        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(application)
        val notificationInterval =
            sharedPrefs.getString(Keys.BEFORE_KEY, "600")?.toIntOrNull() ?: 600

        val dueDate = insertedTodo.dueDate
        val notificationTime = dueDate - notificationInterval * 1000
        if (alarmManager.canScheduleExactAlarms()) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, notificationTime, pendingIntent)
        }
    }
}
