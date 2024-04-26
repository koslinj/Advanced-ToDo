package koslin.jan.todo.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import koslin.jan.todo.R
import koslin.jan.todo.viewmodel.DateTimeViewModel
import java.util.Calendar
import java.util.Date

class DatePickerDialog : DialogFragment() {

    private lateinit var datePicker: DatePicker
    private lateinit var timeButton: FloatingActionButton
    private lateinit var root: View
    private val dateTimeViewModel: DateTimeViewModel by activityViewModels()
    private var year: Int = 0
    private var month: Int = 0
    private var day: Int = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Inflate the layout
        root = LayoutInflater.from(requireContext()).inflate(R.layout.date_picker_layout, null, false)
        datePicker = root.findViewById(R.id.datePicker)
        timeButton = root.findViewById(R.id.timeButton)

        timeButton.setOnClickListener {
            val picker =
                MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(12)
                    .setMinute(10)
                    .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                    .setPositiveButtonText("OK")
                    .setNegativeButtonText(getString(R.string.cancel).uppercase())
                    .build()

            picker.addOnPositiveButtonClickListener {
                val selectedHour = picker.hour
                val selectedMinute = picker.minute

                dateTimeViewModel.updateTime(selectedHour, selectedMinute)
            }

            picker.show(parentFragmentManager, "timePickerTag")
        }

        // Use the current date as the default date in the picker.
        val c = Calendar.getInstance()
        c.time = Date(dateTimeViewModel.selectedDateTime.value!!)
        year = c.get(Calendar.YEAR)
        month = c.get(Calendar.MONTH)
        day = c.get(Calendar.DAY_OF_MONTH)

        // Initialize the DatePicker with the current date
        datePicker.init(year, month, day) { view, year, month, dayOfMonth ->
            onDateSet(year, month, dayOfMonth)
        }

        // Build AlertDialog with custom view
        return AlertDialog.Builder(requireContext())
            .setView(root)
            .setPositiveButton("OK") { dialog, which ->
                dateTimeViewModel.updateDate(year, month, day)
            }
            .setNegativeButton(R.string.cancel) { dialog, which ->

            }
            .create()
    }

    private fun onDateSet(year: Int, month: Int, dayOfMonth: Int) {
        this.year = year
        this.month = month
        this.day = dayOfMonth
    }
}