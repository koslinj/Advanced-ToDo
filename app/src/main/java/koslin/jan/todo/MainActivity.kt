package koslin.jan.todo

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import koslin.jan.todo.adapter.TodoAdapter
import koslin.jan.todo.dialog.ModalBottomSheet
import koslin.jan.todo.dialog.NewTodoDialog
import koslin.jan.todo.entity.Todo
import koslin.jan.todo.fragment.TodoDetailsFragment
import koslin.jan.todo.viewmodel.TodoViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var newTodoButton: FloatingActionButton
    private lateinit var settingsButton: Button
    private lateinit var searchEditText: EditText
    private lateinit var todoAdapter: TodoAdapter
    private lateinit var todoViewModel: TodoViewModel
    private lateinit var itemTouchHelper: ItemTouchHelper

    private lateinit var permissionsHandler: PermissionsHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        todoViewModel = ViewModelProvider(this).get(TodoViewModel::class.java)

        // Handle intent action
        Log.d("INTENT", intent.action.toString())
        if (intent.action == ACTION_SHOW_TODO_DETAILS) {
            val todoStr = intent?.getStringExtra(Notification.EXTRA_TODO) ?: ""
            val todo = Gson().fromJson(todoStr, Todo::class.java)
            if (todo != null) {
                showDetailsFragment(todo)
            }
            intent.action = ""
        }

        newTodoButton = findViewById(R.id.newTodoButton)
        settingsButton = findViewById(R.id.settingsButton)
        searchEditText = findViewById(R.id.searchEditText)
        recyclerView = findViewById(R.id.mainRecyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)
        todoAdapter = TodoAdapter(
            emptyList(),
            onTodoDeleted = { todoViewModel.deleteTodo(it) },
            onTodoClicked = { showDetailsFragment(it) },
            onToggleClicked = { todoViewModel.toggleTodoState(it) }
        )
        recyclerView.adapter = todoAdapter

        itemTouchHelper = ItemTouchHelper(TodoTouchHelper(this, todoAdapter))
        itemTouchHelper.attachToRecyclerView(recyclerView)

        newTodoButton.setOnClickListener {
            val newTodoDialog = NewTodoDialog()
            newTodoDialog.show(supportFragmentManager, "newTodoTag")
        }

        settingsButton.setOnClickListener {
            showBottomSheet()
        }

        todoViewModel.todoList.observe(this) { todos ->
            lifecycleScope.launch(Dispatchers.IO) {
                for (todo in todos){
                    val attachments = App.database.todoDao().getAttachmentsForTodo(todo.id)
                    todo.attachments = attachments
                }
                withContext(Dispatchers.Main){
                    todoAdapter.updateData(todos)
                }
            }
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if(s.toString().isNotEmpty()) {
                    // Perform search operation
                    todoViewModel.searchTodosByTitle(s.toString())
                } else {
                    todoViewModel.publicRefresh()
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        permissionsHandler = PermissionsHandler(this)
        permissionsHandler.handleAllPermissions()
    }

    private fun showBottomSheet() {
        val modalBottomSheet = ModalBottomSheet()
        modalBottomSheet.show(supportFragmentManager, "settingsBottomSheet")
    }


    fun showDetailsFragment(todo: Todo) {
        val oldFragment = supportFragmentManager.findFragmentByTag(TodoDetailsFragment::class.java.simpleName)

        if (oldFragment != null && oldFragment is TodoDetailsFragment) {
            return
        }

        val fragment = TodoDetailsFragment.newInstance(todo)
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                androidx.appcompat.R.anim.abc_slide_in_bottom,
                androidx.appcompat.R.anim.abc_slide_out_bottom,
                androidx.appcompat.R.anim.abc_slide_in_bottom,
                androidx.appcompat.R.anim.abc_slide_out_bottom,
            )
            .replace(R.id.mainContainer, fragment, TodoDetailsFragment::class.java.simpleName)
            .addToBackStack(null)
            .commit()
    }

    companion object {
        const val ACTION_SHOW_TODO_DETAILS = "ACTION_SHOW_TODO_DETAILS"
    }

}