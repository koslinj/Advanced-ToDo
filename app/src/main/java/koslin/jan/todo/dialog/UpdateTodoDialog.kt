package koslin.jan.todo.dialog

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import koslin.jan.todo.R
import koslin.jan.todo.entity.Todo
import koslin.jan.todo.fragment.TodoDetailsFragment
import koslin.jan.todo.viewmodel.DateTimeViewModel
import koslin.jan.todo.viewmodel.TodoViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class UpdateTodoDialog : DialogFragment(R.layout.new_todo_dialog)
{
    private lateinit var saveButton: Button
    private lateinit var dateButton: Button
    private lateinit var dialogHeader: TextView
    private lateinit var title: TextInputEditText
    private lateinit var desc: TextInputEditText
    private lateinit var category: Spinner
    private lateinit var todo: Todo

    private val dateTimeViewModel: DateTimeViewModel by activityViewModels()
    private val todoViewModel: TodoViewModel by activityViewModels()

    companion object {
        private const val ARG_TODO_JSON = "todo_json"

        fun newInstance(todo: Todo): UpdateTodoDialog {
            val fragment = UpdateTodoDialog()
            val args = Bundle()
            args.putString(ARG_TODO_JSON, Gson().toJson(todo))
            fragment.arguments = args
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //dialog?.window?.setLayout(400, ViewGroup.LayoutParams.WRAP_CONTENT)

        todo = Gson().fromJson(requireArguments().getString(ARG_TODO_JSON), Todo::class.java)

        saveButton = view.findViewById(R.id.saveButton)
        dateButton = view.findViewById(R.id.dateButton)
        dialogHeader = view.findViewById(R.id.dialogHeader)
        title = view.findViewById(R.id.title)
        desc = view.findViewById(R.id.desc)
        category = view.findViewById(R.id.categorySpinner)

        dialogHeader.text = requireContext().getString(R.string.update_todo)
        title.setText(todo.title)
        desc.setText(todo.description)

        val categoriesArray = resources.getStringArray(R.array.todo_categories_entries)
        Log.d("TEST", categoriesArray.toString())
        val position = categoriesArray.indexOf(todo.category)
        Log.d("TEST", position.toString())
        if (position != -1) {
            category.setSelection(position)
        } else {
            // Handle the case where the selected item is not found in the array
        }

        saveButton.setOnClickListener {
            saveAction()
        }

        val calendar = Calendar.getInstance().apply {
            timeInMillis = todo.dueDate
        }
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        dateTimeViewModel.updateDate(year, month, dayOfMonth)
        dateTimeViewModel.updateTime(hourOfDay, minute)

        // Inside your fragment
        dateTimeViewModel.selectedDateTime.observe(viewLifecycleOwner) { dateTime ->
            val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

            val dateString = dateFormat.format(dateTime)
            val timeString = timeFormat.format(dateTime)

            dateButton.text = "$dateString $timeString"
        }

        dateButton.setOnClickListener {
            val newFragment = DatePickerDialog()
            newFragment.show(parentFragmentManager, "datePickerTag")
        }
    }

    private fun saveAction()
    {
        val titleStr = title.text.toString()
        val descStr = desc.text.toString()
        val dueDate = dateTimeViewModel.selectedDateTime.value!!
        val cat = category.selectedItem as String

        // Create a copy of the existing todo object with updated values
        val updatedTodo = todo.copy(title = titleStr, description = descStr, dueDate = dueDate, category = cat)
        todoViewModel.updateTodo(updatedTodo)

        (parentFragment as TodoUpdateListener).onTodoUpdated(updatedTodo)

        title.setText("")
        desc.setText("")
        dismiss()
    }

    interface TodoUpdateListener {
        fun onTodoUpdated(todo: Todo)
    }

}