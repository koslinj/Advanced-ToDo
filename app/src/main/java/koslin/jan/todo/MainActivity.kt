package koslin.jan.todo

import android.Manifest
import android.app.AlarmManager
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
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

    private var requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                Log.d("POST_NOTIFICATION_PERMISSION", "USER DENIED PERMISSION")
            } else {
                Log.d("POST_NOTIFICATION_PERMISSION", "USER GRANTED PERMISSION")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        todoViewModel = ViewModelProvider(this).get(TodoViewModel::class.java)

        // Handle intent action
        if (intent.action == ACTION_SHOW_TODO_DETAILS) {
            val todoStr = intent?.getStringExtra(Notification.EXTRA_TODO) ?: ""
            val todo = Gson().fromJson(todoStr, Todo::class.java)
            if (todo != null) {
                showDetailsFragment(todo)
            }
        }

        newTodoButton = findViewById(R.id.newTodoButton)
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

        todoViewModel.todoList.observe(this) { todos ->
            todoAdapter.updateData(todos)
        }

        requestNotificationPermission()

        val alarmManager: AlarmManager = application.getSystemService<AlarmManager>()!!
        if (alarmManager.canScheduleExactAlarms()) {
            // Set exact alarms.
            Toast.makeText(this, "ALARMY I PRZYPOMNIENIA DZIAŁAJĄ", Toast.LENGTH_SHORT).show()
        } else {
            showExplanationAndRequestPermission()
        }

    }

    private fun requestNotificationPermission() {
        val permission = Manifest.permission.POST_NOTIFICATIONS
        when {
            ContextCompat.checkSelfPermission(
                this, permission
            ) != PackageManager.PERMISSION_GRANTED -> {
                requestPermissionLauncher.launch(permission)
            }
        }
    }

    private fun requestScheduleExactAlarmPermission() {
        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
            data = Uri.parse("package:koslin.jan.todo")
        }
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // Obsługa przypadku, gdy urządzenie nie obsługuje akcji ACTION_REQUEST_SCHEDULE_EXACT_ALARM
            Toast.makeText(
                this,
                "Twoje urządzenie nie obsługuje tej funkcji.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun showExplanationAndRequestPermission() {
        AlertDialog.Builder(this)
            .setTitle("Prośba o uprawnienie")
            .setMessage("Aplikacja potrzebuje uprawnienia do planowania dokładnych alarmów w celu zapewnienia dokładnych powiadomień.")
            .setPositiveButton("Przyznaj uprawnienie") { dialog, _ ->
                requestScheduleExactAlarmPermission()
                dialog.dismiss()
            }
            .setNegativeButton("Anuluj") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }


    fun showDetailsFragment(todo: Todo) {
        // Replace the current fragment with a new fragment displaying todo details
        val fragment = TodoDetailsFragment.newInstance(todo)
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

    companion object {
        const val ACTION_SHOW_TODO_DETAILS = "ACTION_SHOW_TODO_DETAILS"
    }

}