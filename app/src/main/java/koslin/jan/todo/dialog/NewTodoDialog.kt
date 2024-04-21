package koslin.jan.todo.dialog

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import koslin.jan.todo.App
import koslin.jan.todo.R
import koslin.jan.todo.entity.Todo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class NewTodoDialog(private val onTodoAdded: (Todo) -> Unit) : DialogFragment(R.layout.new_todo_dialog)
{
    private lateinit var saveButton: Button
    private lateinit var title: TextInputEditText
    private lateinit var desc: TextInputEditText

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //dialog?.window?.setLayout(400, ViewGroup.LayoutParams.WRAP_CONTENT)

        saveButton = view.findViewById(R.id.saveButton)
        title = view.findViewById(R.id.title)
        desc = view.findViewById(R.id.desc)

        saveButton.setOnClickListener {
            saveAction()
        }
    }

    private fun saveAction()
    {
        val titleStr = title.text.toString()
        val descStr = desc.text.toString()
        val dueDate = System.currentTimeMillis() // You may replace this with a DatePicker or similar

        val todo = Todo(title = titleStr, description = descStr, dueDate = dueDate)
        onTodoAdded(todo)

        title.setText("")
        desc.setText("")
        dismiss()
    }

}