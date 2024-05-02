package koslin.jan.todo.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.Gson
import koslin.jan.todo.R
import koslin.jan.todo.entity.Todo
import koslin.jan.todo.viewmodel.TodoViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class TodoDetailsFragment : Fragment(R.layout.fragment_todo_details) {
    private lateinit var titleTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var dateTextView: TextView
    private lateinit var createdAtTextView: TextView
    private lateinit var topAppBar: MaterialToolbar
    private lateinit var todo: Todo
    private val todoViewModel: TodoViewModel by activityViewModels()

    companion object {
        private const val ARG_TODO_JSON = "todo_json"

        fun newInstance(todo: Todo): TodoDetailsFragment {
            val fragment = TodoDetailsFragment()
            val args = Bundle()
            args.putString(ARG_TODO_JSON, Gson().toJson(todo))
            fragment.arguments = args
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        todo = Gson().fromJson(requireArguments().getString(ARG_TODO_JSON), Todo::class.java)

        view.setOnClickListener { } //empty on click to avoid pressing things behind
        topAppBar = view.findViewById(R.id.topAppBar)
        titleTextView = view.findViewById(R.id.titleTextView)
        descriptionTextView = view.findViewById(R.id.descriptionTextView)
        dateTextView = view.findViewById(R.id.dateTextView)
        createdAtTextView = view.findViewById(R.id.createdAtTextView)

        topAppBar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val initialIcon = if (todo.notification) R.drawable.notifications_active_icon else R.drawable.notifications_off_icon
        val initialChecked = todo.notification

        // Find the MenuItem corresponding to notificationsToggle
        val notificationsMenuItem = topAppBar.menu.findItem(R.id.notificationsToggle)

        // Set initial icon and checked state
        notificationsMenuItem.setIcon(initialIcon)
        notificationsMenuItem.isChecked = initialChecked

        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.notificationsToggle -> {
                    // Toggle the icon based on the current state
                    if (menuItem.isChecked) {
                        Log.d("MENU_", "ZMIENIA NA OFF")
                        menuItem.setIcon(R.drawable.notifications_off_icon)
                        menuItem.isChecked = false
                    } else {
                        menuItem.isChecked = true
                        Log.d("MENU_", "ZMIENIA NA ON")
                        menuItem.setIcon(R.drawable.notifications_active_icon)
                    }
                    todoViewModel.toggleTodoNotifications(todo)
                    true
                }

                R.id.edit -> {
                    // Handle edit text press
                    true
                }

                R.id.delete -> {
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle(requireContext().getString(R.string.delete_task))
                    builder.setMessage(requireContext().getString(R.string.delete_task_question))
                    builder.setPositiveButton(
                        requireContext().getString(R.string.confirm)
                    ) { dialog, which ->
                        todoViewModel.deleteTodo(todo)
                        parentFragmentManager.popBackStack()
                    }
                    builder.setNegativeButton(
                        requireContext().getString(R.string.cancel)
                    ) { dialog, which -> }
                    builder.setOnCancelListener { dialog -> }
                    val dialog = builder.create()
                    dialog.show()
                    true
                }

                else -> false
            }
        }

        displayTodoDetails()
    }

    private fun displayTodoDetails() {

        val dateFormat1 = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
        val dateFormat2 = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        titleTextView.text = todo.title
        descriptionTextView.text = todo.description
        var dateString = dateFormat1.format(todo.dueDate)
        var timeString = timeFormat.format(todo.dueDate)
        dateTextView.text = "$dateString $timeString"
        dateString = dateFormat2.format(todo.createdAt)
        timeString = timeFormat.format(todo.createdAt)
        createdAtTextView.text = "$dateString\n$timeString"
    }
}
