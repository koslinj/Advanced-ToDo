package koslin.jan.todo.fragment
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import koslin.jan.todo.R
import java.text.SimpleDateFormat
import java.util.Locale

class TodoDetailsFragment : Fragment(R.layout.fragment_todo_details) {
    private lateinit var titleTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var dateTextView: TextView

    companion object {
        private const val ARG_TITLE = "title"
        private const val ARG_DESCRIPTION = "description"
        private const val ARG_DUE_DATE = "due_date"

        fun newInstance(title: String, description: String, dueDate: Long): TodoDetailsFragment {
            val fragment = TodoDetailsFragment()
            val args = Bundle()
            args.putString(ARG_TITLE, title)
            args.putString(ARG_DESCRIPTION, description)
            args.putLong(ARG_DUE_DATE, dueDate)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnClickListener {  } //empty on click to avoid pressing things behind
        titleTextView = view.findViewById(R.id.titleTextView)
        descriptionTextView = view.findViewById(R.id.descriptionTextView)
        dateTextView = view.findViewById(R.id.dateTextView)
        displayTodoDetails()
    }

    private fun displayTodoDetails() {
        val title = arguments?.getString(ARG_TITLE)
        val description = arguments?.getString(ARG_DESCRIPTION)
        val dueDate = arguments?.getLong(ARG_DUE_DATE)

        val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val dateString = dateFormat.format(dueDate)
        val timeString = timeFormat.format(dueDate)

        titleTextView.text = title
        descriptionTextView.text = description
        dateTextView.text = "$dateString $timeString"
    }
}
