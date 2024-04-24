package koslin.jan.todo

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import koslin.jan.todo.viewmodel.DateViewModel
import java.util.Calendar
import java.util.Date

class DatePickerFragment : DialogFragment() {

    private lateinit var datePicker: DatePicker
    private lateinit var root: View
    private val dateViewModel: DateViewModel by activityViewModels()
    private var selectedDate: Long = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Inflate the layout
        root = LayoutInflater.from(requireContext()).inflate(R.layout.date_picker_layout, null, false)
        datePicker = root.findViewById(R.id.datePicker)

        // Use the current date as the default date in the picker.
        val c = Calendar.getInstance()
        c.time = Date(dateViewModel.selectedDate.value!!)
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        selectedDate = dateViewModel.selectedDate.value!!

        // Initialize the DatePicker with the current date
        datePicker.init(year, month, day) { view, year, month, dayOfMonth ->
            onDateSet(view, year, month, dayOfMonth)
        }

        // Build AlertDialog with custom view
        return AlertDialog.Builder(requireContext())
            .setView(root)
            .setPositiveButton("OK") { dialog, which ->
                dateViewModel.setSelectedDate(selectedDate)
            }
            .setNegativeButton("Cancel") { dialog, which ->

            }
            .create()
    }

    private fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        // Convert selected date to milliseconds
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        selectedDate = calendar.timeInMillis
    }
}