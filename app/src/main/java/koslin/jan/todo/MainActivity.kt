package koslin.jan.todo

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import koslin.jan.todo.dialog.NewTodoDialog
import koslin.jan.todo.entity.Todo
import koslin.jan.todo.fragment.TodoDetailsFragment
import koslin.jan.todo.viewmodel.TodoViewModel


class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var newTodoButton: FloatingActionButton
    private lateinit var todoAdapter: TodoAdapter
    private lateinit var todoViewModel: TodoViewModel
    private lateinit var itemTouchHelper: ItemTouchHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        todoViewModel = ViewModelProvider(this).get(TodoViewModel::class.java)

        newTodoButton = findViewById(R.id.newTodoButton)
        recyclerView = findViewById(R.id.mainRecyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)
        todoAdapter = TodoAdapter(
            emptyList(),
            onTodoDeleted = { todoViewModel.deleteTodo(it) },
            onTodoClicked = { onTodoClicked(it) }
        )
        recyclerView.adapter = todoAdapter

        itemTouchHelper = ItemTouchHelper(TodoTouchHelper(this, todoAdapter))
        itemTouchHelper.attachToRecyclerView(recyclerView)

        newTodoButton.setOnClickListener {
            val newTodoDialog = NewTodoDialog()
            newTodoDialog.show(supportFragmentManager, "newTodoTag")
        }

        todoViewModel.todoList.observe(this) { todos ->
            todoAdapter.updateData(todos)
        }
    }

    fun onTodoClicked(todo: Todo) {
        // Replace the current fragment with a new fragment displaying todo details
        val fragment = TodoDetailsFragment.newInstance(todo.title, todo.description, todo.dueDate)
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                androidx.appcompat.R.anim.abc_slide_in_bottom,
                androidx.appcompat.R.anim.abc_slide_out_bottom,
                androidx.appcompat.R.anim.abc_slide_in_bottom,
                androidx.appcompat.R.anim.abc_slide_out_bottom,
            )
            .replace(R.id.mainContainer, fragment)
            .addToBackStack(null)
            .commit()
    }

}