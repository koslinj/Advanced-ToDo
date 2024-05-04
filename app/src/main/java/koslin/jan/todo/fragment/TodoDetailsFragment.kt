package koslin.jan.todo.fragment

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.gson.Gson
import koslin.jan.todo.App
import koslin.jan.todo.adapter.ImageAdapter
import koslin.jan.todo.R
import koslin.jan.todo.dialog.UpdateTodoDialog
import koslin.jan.todo.entity.Attachment
import koslin.jan.todo.entity.Todo
import koslin.jan.todo.viewmodel.TodoViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

class TodoDetailsFragment : Fragment(R.layout.fragment_todo_details),
    UpdateTodoDialog.TodoUpdateListener {
    private lateinit var titleTextView: TextView
    private lateinit var categoryTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var dateTextView: TextView
    private lateinit var createdAtTextView: TextView
    private lateinit var topAppBar: MaterialToolbar
    private lateinit var notificationsMenuItem: MenuItem
    private lateinit var todo: Todo
    private lateinit var attachments: List<Attachment>
    private val todoViewModel: TodoViewModel by activityViewModels()
    private val todoDao = App.database.todoDao()
    private val imageIds = mutableListOf<Long>()

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
        lifecycleScope.launch(Dispatchers.IO) {
            attachments = todoDao.getAttachmentsForTodo(todo.id)
            Log.d("ATTACH", attachments.toString())
            todo.attachments = attachments

            withContext(Dispatchers.Main) {
                loadImagesIds(attachments)
                // Set up RecyclerView
                val recyclerView: RecyclerView = view.findViewById(R.id.filesRecyclerView)
                val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                val adapter = ImageAdapter(requireContext(), imageIds)
                recyclerView.layoutManager = layoutManager
                recyclerView.adapter = adapter
            }
        }

        view.setOnClickListener { } //empty on click to avoid pressing things behind
        topAppBar = view.findViewById(R.id.topAppBar)
        notificationsMenuItem = topAppBar.menu.findItem(R.id.notificationsToggle)
        titleTextView = view.findViewById(R.id.titleTextView)
        categoryTextView = view.findViewById(R.id.categoryTextView)
        descriptionTextView = view.findViewById(R.id.descriptionTextView)
        dateTextView = view.findViewById(R.id.dateTextView)
        createdAtTextView = view.findViewById(R.id.createdAtTextView)

        topAppBar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val initialNotifIcon =
            if (todo.notification) R.drawable.notifications_active_icon
            else R.drawable.notifications_off_icon
        val initialNotifChecked = todo.notification
        changeNotificationIconState(initialNotifIcon, initialNotifChecked)

        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.notificationsToggle -> {
                    // Toggle the icon based on the current state
                    if (menuItem.isChecked) {
                        changeNotificationIconState(R.drawable.notifications_off_icon, false)
                        todoViewModel.turnOffTodoNotifications(todo)
                    } else {
                        changeNotificationIconState(R.drawable.notifications_active_icon, true)
                        todoViewModel.turnOnTodoNotifications(todo)
                    }
                    true
                }

                R.id.edit -> {
                    showUpdateTodoDialog(todo)
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

    fun showUpdateTodoDialog(todo: Todo) {
        val dialog = UpdateTodoDialog.newInstance(todo)
        dialog.show(childFragmentManager, "newTodoTag")
    }

    private fun changeNotificationIconState(icon: Int, checked: Boolean) {
        notificationsMenuItem.setIcon(icon)
        notificationsMenuItem.isChecked = checked
    }

    fun loadImagesIds(attachments: List<Attachment>) {
        for (att in attachments){
            val attUri = Uri.parse(att.uri)
            val attFinalStr = attUri.lastPathSegment.toString().substring(6)
            val finalId = attFinalStr.toLong()
            imageIds.add(finalId)
        }
    }


    private fun displayTodoDetails() {

        val dateFormat1 = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
        val dateFormat2 = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        titleTextView.text = todo.title

        val catPrefixStr = requireContext().getString(R.string.category)
        val catEntries = resources.getStringArray(R.array.todo_categories_entries)
        val catValues = resources.getStringArray(R.array.todo_categories_values)
        val pos = catValues.indexOf(todo.category)
        val cat = catEntries[pos]
        categoryTextView.text = "$catPrefixStr: $cat"

        descriptionTextView.text = todo.description
        var dateString = dateFormat1.format(todo.dueDate)
        var timeString = timeFormat.format(todo.dueDate)
        dateTextView.text = "$dateString $timeString"
        dateString = dateFormat2.format(todo.createdAt)
        timeString = timeFormat.format(todo.createdAt)
        createdAtTextView.text = "$dateString\n$timeString"
    }

    override fun onTodoUpdated(todo: Todo) {
        this.todo = todo
        displayTodoDetails()
    }
}
