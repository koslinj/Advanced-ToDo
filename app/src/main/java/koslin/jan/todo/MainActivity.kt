package koslin.jan.todo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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

        newTodoButton.setOnClickListener {
//            MaterialAlertDialogBuilder(this)
//                .setView(R.layout.new_todo_dialog)
//                .show()
            NewTodoDialog().show(supportFragmentManager, "newTodoTag")
        }

        GlobalScope.launch(Dispatchers.IO) {
//            val todo = Todo(0, "todo 2", "Description 2", System.currentTimeMillis())
//            App.database.todoDao().insert(todo);

            val todoList = App.database.todoDao().getAllTodos()
            withContext(Dispatchers.Main) {
                todoAdapter = TodoAdapter(todoList)
                recyclerView.adapter = todoAdapter
            }
        }
    }
}