package koslin.jan.todo.dialog

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Spinner
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import koslin.jan.todo.R
import koslin.jan.todo.adapter.TempImageAdapter
import koslin.jan.todo.config.Keys
import koslin.jan.todo.entity.Attachment
import koslin.jan.todo.entity.Todo
import koslin.jan.todo.viewmodel.DateTimeViewModel
import koslin.jan.todo.viewmodel.TodoViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
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
    private lateinit var tempImageAdapter: TempImageAdapter

    private val attachmentFilePaths = mutableListOf<String>()

    private val dateTimeViewModel: DateTimeViewModel by activityViewModels()
    private val todoViewModel: TodoViewModel by activityViewModels()

    private val getContent = registerForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris: List<Uri> ->
        val attachmentUris = mutableListOf<Uri>()
        attachmentUris.addAll(uris)
        Log.d("FILES", attachmentUris.toString())
        val filePaths = saveFilesToInternalStorage(attachmentUris)
        //attachmentFilePaths.clear()
        attachmentFilePaths.addAll(filePaths)
        tempImageAdapter.updateData(attachmentFilePaths)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putStringArrayList(Keys.FILE_PATHS_KEY, ArrayList(attachmentFilePaths))
    }

    private fun saveFilesToInternalStorage(uris: List<Uri>): List<String> {
        val filePaths = mutableListOf<String>()
        uris.forEach { uri ->
            val fileName = getFileNameFromUri(uri)
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            inputStream?.let {
                val filePath = saveFile(fileName, it)
                filePaths.add(filePath)
            }
        }
        return filePaths
    }

    private fun getFileNameFromUri(uri: Uri): String {
        var fileName = "unknown_file"
        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                var index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if(index == -1) index = 0
                val originalFileName = it.getString(index) ?: "unknown_file"

                val currentTime = System.currentTimeMillis()

                fileName = originalFileName.let {
                    val dotIndex = it.lastIndexOf('.')
                    if (dotIndex != -1) {
                        "${it.substring(0, dotIndex)}_$currentTime${it.substring(dotIndex)}"
                    } else {
                        "${it}_$currentTime"
                    }
                }
            }
        }
        return fileName
    }

    private fun saveFile(fileName: String, inputStream: InputStream): String {
        val file = File(requireContext().filesDir, fileName)
        FileOutputStream(file).use { outputStream ->
            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }
        }
        Log.d("FILES", "File saved: ${file.absolutePath}")
        return file.absolutePath
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            val screenHeight = Resources.getSystem().displayMetrics.heightPixels
            dialog?.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, screenHeight-40)
        }

        saveButton = view.findViewById(R.id.saveButton)
        dateButton = view.findViewById(R.id.dateButton)
        title = view.findViewById(R.id.title)
        desc = view.findViewById(R.id.desc)
        category = view.findViewById(R.id.categorySpinner)
        attachmentButton = view.findViewById(R.id.attachmentButton)
        attachmentButton.setOnClickListener {
            getContent.launch(arrayOf("*/*"))
        }

        // Set up RecyclerView
        val attachmentsRecyclerView = view.findViewById<RecyclerView>(R.id.attachmentsRecyclerView)
        attachmentsRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        // Load paths saved in instance state
        savedInstanceState?.getStringArrayList(Keys.FILE_PATHS_KEY)?.let { filePaths ->
            attachmentFilePaths.addAll(filePaths)
        }

        // Set adapter
        tempImageAdapter = TempImageAdapter(requireContext(), attachmentFilePaths) {
            val file = File(attachmentFilePaths[it])
            if (file.exists()) {
                file.delete()
            }
            attachmentFilePaths.apply { removeAt(it) }
        }
        attachmentsRecyclerView.adapter = tempImageAdapter

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

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)

        for(path in attachmentFilePaths){
            val file = File(path)
            if (file.exists()) {
                file.delete()
            }
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
        val attachments = createAttachmentsFromPaths()
        todoViewModel.addTodoWithAttachments(todo, attachments)

        title.setText("")
        desc.setText("")
        dismiss()
    }

    private fun createAttachmentsFromPaths(): List<Attachment> {
        val attachments = mutableListOf<Attachment>()
        for (path in attachmentFilePaths) {
            val attachment = Attachment(todoId = 0, uri = path) // Assuming todoId needs to be set later
            attachments.add(attachment)
        }
        return attachments
    }

}