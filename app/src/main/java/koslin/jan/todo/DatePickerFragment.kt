package koslin.jan.todo

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.Calendar

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    private lateinit var datePicker: DatePicker
    private lateinit var root: View

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Inflate the layout
        root = LayoutInflater.from(requireContext()).inflate(R.layout.date_picker_layout, null, false)
        datePicker = root.findViewById(R.id.datePicker)

        // Use the current date as the default date in the picker.
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        // Initialize the DatePicker with the current date
        datePicker.init(year, month, day, null)

        // Build AlertDialog with custom view
        return AlertDialog.Builder(requireContext())
            .setView(root)
            .setPositiveButton("OK") { dialog, which ->
                // Handle OK button click if needed
            }
            .setNegativeButton("Cancel") { dialog, which ->
                // Handle Cancel button click if needed
            }
            .create()
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        // Do something with the date the user picks.
    }
}