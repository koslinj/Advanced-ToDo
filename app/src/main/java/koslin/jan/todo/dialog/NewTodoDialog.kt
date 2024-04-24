package koslin.jan.todo.dialog

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import koslin.jan.todo.App
import koslin.jan.todo.DatePickerFragment
import koslin.jan.todo.R
import koslin.jan.todo.entity.Todo
import koslin.jan.todo.viewmodel.DateViewModel
import koslin.jan.todo.viewmodel.TodoViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class NewTodoDialog : DialogFragment(R.layout.new_todo_dialog)
{
    private lateinit var saveButton: Button
    private lateinit var dateButton: Button
    private lateinit var title: TextInputEditText
    private lateinit var desc: TextInputEditText

    private val dateViewModel: DateViewModel by activityViewModels()
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

        dateViewModel.selectedDate.observe(viewLifecycleOwner) { date ->
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateButton.text = dateFormat.format(date)
        }

        dateButton.setOnClickListener {
            val newFragment = DatePickerFragment()
            newFragment.show(parentFragmentManager, "datePickerTag")
        }
    }

    private fun saveAction()
    {
        val titleStr = title.text.toString()
        val descStr = desc.text.toString()
        val dueDate = dateViewModel.selectedDate.value!!

        val todo = Todo(title = titleStr, description = descStr, dueDate = dueDate)
        todoViewModel.addTodo(todo)

        title.setText("")
        desc.setText("")
        dismiss()
    }

}