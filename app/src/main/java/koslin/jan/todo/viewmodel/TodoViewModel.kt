package koslin.jan.todo.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import koslin.jan.todo.App
import koslin.jan.todo.entity.Todo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TodoViewModel(application: Application) : AndroidViewModel(application) {
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
            todoDao.insert(todo)
            todoList = todoDao.getAllTodosLiveData();
        }
    }
}
