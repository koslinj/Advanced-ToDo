package koslin.jan.todo.dialog

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.textfield.TextInputEditText
import koslin.jan.todo.R
import koslin.jan.todo.entity.Todo
import koslin.jan.todo.viewmodel.DateTimeViewModel
import koslin.jan.todo.viewmodel.TodoViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class NewTodoDialog : DialogFragment(R.layout.new_todo_dialog)
{
    private lateinit var saveButton: Button
    private lateinit var dateButton: Button
    private lateinit var title: TextInputEditText
    private lateinit var desc: TextInputEditText

    private val dateTimeViewModel: DateTimeViewModel by activityViewModels()
    private val todoViewModel: TodoViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //dialog?.window?.setLayout(400, ViewGroup.LayoutParams.WRAP_CONTENT)

        saveButton = view.findViewById(R.id.saveButton)
        dateButton = view.findViewById(R.id.dateButton)
        title = view.findViewById(R.id.title)
        desc = view.findViewById(R.id.desc)

        saveButton.setOnClickListener {
            saveAction()
        }

//        dateTimeViewModel.selectedDateTime.observe(viewLifecycleOwner) { date ->
//            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//            dateButton.text = dateFormat.format(date)
//        }

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

        val todo = Todo(title = titleStr, description = descStr, dueDate = dueDate)
        todoViewModel.addTodo(todo)

        title.setText("")
        desc.setText("")
        dismiss()
    }

}