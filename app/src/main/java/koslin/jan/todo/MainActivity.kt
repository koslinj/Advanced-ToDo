package koslin.jan.todo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import koslin.jan.todo.entity.Todo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var todoAdapter: TodoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.mainRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

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