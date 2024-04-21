package koslin.jan.todo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import koslin.jan.todo.dialog.NewTodoDialog
import koslin.jan.todo.entity.Todo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var newTodoButton: FloatingActionButton
    private lateinit var todoAdapter: TodoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        newTodoButton = findViewById(R.id.newTodoButton)
        recyclerView = findViewById(R.id.mainRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        todoAdapter = TodoAdapter(emptyList()) { todo ->
            deleteTodo(todo)
        }
        recyclerView.adapter = todoAdapter

        val itemTouchHelper = ItemTouchHelper(TodoTouchHelper(this, todoAdapter))
        itemTouchHelper.attachToRecyclerView(recyclerView)

        newTodoButton.setOnClickListener {
            val newTodoDialog = NewTodoDialog {
                refreshAllTodos()
            }

            newTodoDialog.show(supportFragmentManager, "newTodoTag")
        }

        refreshAllTodos()
    }

    private fun deleteTodo(todo: Todo) {
        GlobalScope.launch(Dispatchers.IO) {
            App.database.todoDao().delete(todo)
            val todoList = App.database.todoDao().getAllTodos()
            withContext(Dispatchers.Main) {
                todoAdapter.updateData(todoList) // Update adapter with actual data
            }
        }
    }

    private fun refreshAllTodos() {
        GlobalScope.launch(Dispatchers.IO) {
            val todoList = App.database.todoDao().getAllTodos()
            withContext(Dispatchers.Main) {
                todoAdapter.updateData(todoList) // Update adapter with actual data
            }
        }
    }
}