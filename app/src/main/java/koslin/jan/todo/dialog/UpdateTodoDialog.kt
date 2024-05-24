package koslin.jan.todo.dialog

import android.content.DialogInterface
import android.content.res.Configuration
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import koslin.jan.todo.App
import koslin.jan.todo.R
import koslin.jan.todo.adapter.ImageAdapter
import koslin.jan.todo.adapter.TempImageAdapter
import koslin.jan.todo.config.Keys
import koslin.jan.todo.entity.Attachment
import koslin.jan.todo.entity.Todo
import koslin.jan.todo.viewmodel.DateTimeViewModel
import koslin.jan.todo.viewmodel.TodoViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class UpdateTodoDialog : DialogFragment(R.layout.new_todo_dialog) {
    private lateinit var saveButton: Button
    private lateinit var dateButton: Button
    private lateinit var attachmentButton: Button
    private lateinit var dialogHeader: TextView
    private lateinit var title: TextInputEditText
    private lateinit var desc: TextInputEditText
    private lateinit var category: Spinner
    private lateinit var todo: Todo
    private lateinit var tempImageAdapter: TempImageAdapter

    private val attachmentFilePaths = mutableListOf<String>()
    private val toRemovePaths = mutableListOf<String>()

    private val todoDao = App.database.todoDao()
    private val dateTimeViewModel: DateTimeViewModel by activityViewModels()
    private val todoViewModel: TodoViewModel by activityViewModels()

    private val getContent =
        registerForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris: List<Uri> ->
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
        outState.putStringArrayList(Keys.REMOVE_PATHS_KEY, ArrayList(toRemovePaths))
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
                if (index == -1) index = 0
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
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            val screenHeight = Resources.getSystem().displayMetrics.heightPixels
            dialog?.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, screenHeight - 40)
        }

        todo = Gson().fromJson(requireArguments().getString(ARG_TODO_JSON), Todo::class.java)

        saveButton = view.findViewById(R.id.saveButton)
        dateButton = view.findViewById(R.id.dateButton)
        dialogHeader = view.findViewById(R.id.dialogHeader)
        title = view.findViewById(R.id.title)
        desc = view.findViewById(R.id.desc)
        category = view.findViewById(R.id.categorySpinner)
        attachmentButton = view.findViewById(R.id.attachmentButton)
        attachmentButton.setOnClickListener {
            getContent.launch(arrayOf("*/*"))
        }

        // Set up RecyclerView
        val attachmentsRecyclerView = view.findViewById<RecyclerView>(R.id.attachmentsRecyclerView)
        attachmentsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        // Load existing attachments
        attachmentFilePaths.clear()
        val existingAttachments =
            todo.attachments.map { it.uri } // Assuming `todo.attachments` contains the attachments
        attachmentFilePaths.addAll(existingAttachments)

        // Load paths saved in instance state
        savedInstanceState?.getStringArrayList(Keys.FILE_PATHS_KEY)?.let { filePaths ->
            val newFilePaths = filePaths.filterNot { path -> existingAttachments.contains(path) }
            attachmentFilePaths.addAll(newFilePaths)
        }
        savedInstanceState?.getStringArrayList(Keys.REMOVE_PATHS_KEY)?.let { filePaths ->
            toRemovePaths.addAll(filePaths)
        }
        attachmentFilePaths.removeAll { it in toRemovePaths }

        // Set adapter
        tempImageAdapter = TempImageAdapter(requireContext(), attachmentFilePaths) {
            toRemovePaths.add(attachmentFilePaths[it])
            attachmentFilePaths.apply { removeAt(it) }
        }
        attachmentsRecyclerView.adapter = tempImageAdapter

        dialogHeader.text = requireContext().getString(R.string.update_todo)
        title.setText(todo.title)
        desc.setText(todo.description)

        val categoriesArray = resources.getStringArray(R.array.todo_categories_values)
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

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        Log.d("AAA", attachmentFilePaths.toString())
        lifecycleScope.launch(Dispatchers.IO) {
            var savedAtts = todoDao.getAllAttachments()
            var savedPaths = savedAtts.map { it.uri }
            for (p in attachmentFilePaths) {
                if (!savedPaths.contains(p)) {
                    val file = File(p)
                    if (file.exists()) {
                        file.delete()
                    }
                }
            }
            toRemovePaths.clear()
        }
    }

    private fun saveAction() {
        val titleStr = title.text.toString()
        val descStr = desc.text.toString()
        val dueDate = dateTimeViewModel.selectedDateTime.value!!

        val catEntries = resources.getStringArray(R.array.todo_categories_entries)
        val catValues = resources.getStringArray(R.array.todo_categories_values)
        val pos = catEntries.indexOf(category.selectedItem)
        val cat = catValues[pos]

        val updatedTodo =
            todo.copy(title = titleStr, description = descStr, dueDate = dueDate, category = cat)
        val attachments = createAttachmentsFromPaths()
        val listener = (parentFragment as TodoUpdateListener)
        todoViewModel.updateTodoWithAttachments(updatedTodo, attachments) { _todo, _atts ->
            listener.onTodoUpdated(_todo, _atts)
        }

        for (path in toRemovePaths) {
            val file = File(path)
            if (file.exists()) {
                file.delete()
            }
        }

        title.setText("")
        desc.setText("")
        dismiss()
    }

    private fun createAttachmentsFromPaths(): List<Attachment> {
        val attachments = mutableListOf<Attachment>()
        for (path in attachmentFilePaths) {
            val attachment =
                Attachment(todoId = 0, uri = path) // Assuming todoId needs to be set later
            attachments.add(attachment)
        }
        return attachments
    }

    interface TodoUpdateListener {
        fun onTodoUpdated(todo: Todo, attachments: List<Attachment>)
    }

}