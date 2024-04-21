package koslin.jan.todo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import koslin.jan.todo.dialog.NewTodoDialog
import koslin.jan.todo.viewmodel.TodoViewModel


class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var newTodoButton: FloatingActionButton
    private lateinit var todoAdapter: TodoAdapter
    private lateinit var todoViewModel: TodoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        todoViewModel = ViewModelProvider(this).get(TodoViewModel::class.java)

        newTodoButton = findViewById(R.id.newTodoButton)
        recyclerView = findViewById(R.id.mainRecyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)
        todoAdapter = TodoAdapter(emptyList()) {
            todoViewModel.deleteTodo(it)
        }
        recyclerView.adapter = todoAdapter

        val itemTouchHelper = ItemTouchHelper(TodoTouchHelper(this, todoAdapter))
        itemTouchHelper.attachToRecyclerView(recyclerView)

        newTodoButton.setOnClickListener {
            val newTodoDialog = NewTodoDialog {
                todoViewModel.addTodo(it)
            }
            newTodoDialog.show(supportFragmentManager, "newTodoTag")
        }

        todoViewModel.todoList.observe(this) { todos ->
            todoAdapter.updateData(todos)
        }
    }

}