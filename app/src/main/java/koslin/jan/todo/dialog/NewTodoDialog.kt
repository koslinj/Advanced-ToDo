package koslin.jan.todo.dialog

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Spinner
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.textfield.TextInputEditText
import koslin.jan.todo.R
import koslin.jan.todo.entity.Attachment
import koslin.jan.todo.entity.Todo
import koslin.jan.todo.viewmodel.DateTimeViewModel
import koslin.jan.todo.viewmodel.TodoViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class NewTodoDialog : DialogFragment(R.layout.new_todo_dialog)
{
    private lateinit var saveButton: Button
    private lateinit var dateButton: Button
    private lateinit var attachmentButton: Button
    private lateinit var title: TextInputEditText
    private lateinit var desc: TextInputEditText
    private lateinit var category: Spinner

    private val attachmentUris = mutableListOf<Uri>()

    private val dateTimeViewModel: DateTimeViewModel by activityViewModels()
    private val todoViewModel: TodoViewModel by activityViewModels()

    val getContent = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris: List<Uri> ->
        attachmentUris.clear()
        attachmentUris.addAll(uris)
        Log.d("FILES", attachmentUris.toString())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//        }

        saveButton = view.findViewById(R.id.saveButton)
        dateButton = view.findViewById(R.id.dateButton)
        title = view.findViewById(R.id.title)
        desc = view.findViewById(R.id.desc)
        category = view.findViewById(R.id.categorySpinner)
        attachmentButton = view.findViewById(R.id.attachmentButton)
        attachmentButton.setOnClickListener {
            getContent.launch("*/*")
        }

        saveButton.setOnClickListener {
            saveAction()
        }


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

        val catEntries = resources.getStringArray(R.array.todo_categories_entries)
        val catValues = resources.getStringArray(R.array.todo_categories_values)
        val pos = catEntries.indexOf(category.selectedItem)
        val cat = catValues[pos]

        val todo = Todo(title = titleStr, description = descStr, dueDate = dueDate, category = cat)
        //todoViewModel.addTodo(todo)
        val attachments = createAttachmentsFromUris()
        todoViewModel.addTodoWithAttachments(todo, attachments)

        title.setText("")
        desc.setText("")
        dismiss()
    }

    private fun createAttachmentsFromUris(): List<Attachment> {
        val attachments = mutableListOf<Attachment>()
        for (uri in attachmentUris) {
            // Assuming you have a method to get the URI string
            val uriString = uri.toString()
            val attachment = Attachment(todoId = 0, uri = uriString) // Assuming todoId needs to be set later
            attachments.add(attachment)
        }
        return attachments
    }

}