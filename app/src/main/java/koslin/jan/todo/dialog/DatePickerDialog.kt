package koslin.jan.todo.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import koslin.jan.todo.R
import koslin.jan.todo.viewmodel.DateTimeViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DatePickerDialog : DialogFragment(R.layout.date_picker_layout) {

    private lateinit var datePicker: DatePicker
    private lateinit var timeButton: FloatingActionButton
    private lateinit var timeTextView: TextView
    private lateinit var negativeButton: Button
    private lateinit var positiveButton: Button
    private val dateTimeViewModel: DateTimeViewModel by activityViewModels()
    private var initialDateTime: Long = 0
    private var confirmed = false
    private var year: Int = 0
    private var month: Int = 0
    private var day: Int = 0

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

        if (!confirmed){
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = initialDateTime
            val resetHour = calendar.get(Calendar.HOUR_OF_DAY)
            val resetMinute = calendar.get(Calendar.MINUTE)
            dateTimeViewModel.updateTime(resetHour, resetMinute)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        confirmed = false
        datePicker = view.findViewById(R.id.datePicker)
        timeButton = view.findViewById(R.id.timeButton)
        timeTextView = view.findViewById(R.id.timeTextView)
        negativeButton = view.findViewById(R.id.negativeButton)
        positiveButton = view.findViewById(R.id.positiveButton)

        initialDateTime = dateTimeViewModel.selectedDateTime.value!!

        dateTimeViewModel.selectedDateTime.observe(viewLifecycleOwner) { dateTime ->
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val timeString = timeFormat.format(dateTime)
            timeTextView.text = timeString
        }

        // Use the current date as the default date in the picker.
        val c = Calendar.getInstance()
        c.time = Date(dateTimeViewModel.selectedDateTime.value!!)
        year = c.get(Calendar.YEAR)
        month = c.get(Calendar.MONTH)
        day = c.get(Calendar.DAY_OF_MONTH)
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        negativeButton.setOnClickListener {
            dismiss()
        }

        positiveButton.setOnClickListener {
            dateTimeViewModel.updateDate(year, month, day)
            confirmed = true
            dismiss()
        }

        timeButton.setOnClickListener {
            val picker =
                MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(hour)
                    .setMinute(minute)
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

        // Initialize the DatePicker with the current date
        datePicker.init(year, month, day) { view, year, month, dayOfMonth ->
            onDateSet(year, month, dayOfMonth)
        }

    }

    private fun onDateSet(year: Int, month: Int, dayOfMonth: Int) {
        this.year = year
        this.month = month
        this.day = dayOfMonth
    }
}