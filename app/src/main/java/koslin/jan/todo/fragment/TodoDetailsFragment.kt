package koslin.jan.todo.fragment
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import koslin.jan.todo.R
import java.text.SimpleDateFormat
import java.util.Locale

class TodoDetailsFragment : Fragment(R.layout.fragment_todo_details) {
    private lateinit var titleTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var dateTextView: TextView
    private lateinit var createdAtTextView: TextView
    private lateinit var topAppBar: MaterialToolbar

    companion object {
        private const val ARG_TITLE = "title"
        private const val ARG_DESCRIPTION = "description"
        private const val ARG_DUE_DATE = "due_date"
        private const val ARG_CREATED_AT_DATE = "created_at_date"

        fun newInstance(title: String, description: String, dueDate: Long, createdAt: Long): TodoDetailsFragment {
            val fragment = TodoDetailsFragment()
            val args = Bundle()
            args.putString(ARG_TITLE, title)
            args.putString(ARG_DESCRIPTION, description)
            args.putLong(ARG_DUE_DATE, dueDate)
            args.putLong(ARG_CREATED_AT_DATE, createdAt)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnClickListener {  } //empty on click to avoid pressing things behind
        topAppBar = view.findViewById(R.id.topAppBar)
        titleTextView = view.findViewById(R.id.titleTextView)
        descriptionTextView = view.findViewById(R.id.descriptionTextView)
        dateTextView = view.findViewById(R.id.dateTextView)
        createdAtTextView = view.findViewById(R.id.createdAtTextView)

        topAppBar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.edit -> {
                    // Handle edit text press
                    true
                }
                R.id.delete -> {
                    // Handle favorite icon press
                    true
                }
                else -> false
            }
        }

        displayTodoDetails()
    }

    private fun displayTodoDetails() {
        val title = arguments?.getString(ARG_TITLE)
        val description = arguments?.getString(ARG_DESCRIPTION)
        val dueDate = arguments?.getLong(ARG_DUE_DATE)
        val createdAtDate = arguments?.getLong(ARG_CREATED_AT_DATE)

        val dateFormat1 = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
        val dateFormat2 = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        titleTextView.text = title
        descriptionTextView.text = description
        var dateString = dateFormat1.format(dueDate)
        var timeString = timeFormat.format(dueDate)
        dateTextView.text = "$dateString $timeString"
        dateString = dateFormat2.format(createdAtDate)
        timeString = timeFormat.format(createdAtDate)
        createdAtTextView.text = "$dateString\n$timeString"
    }
}
