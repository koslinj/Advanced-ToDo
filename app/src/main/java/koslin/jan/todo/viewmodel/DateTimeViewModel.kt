package koslin.jan.todo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.Calendar

class DateTimeViewModel : ViewModel() {
    private val _selectedDateTime = MutableLiveData<Long>()
    val selectedDateTime: LiveData<Long> get() = _selectedDateTime

    init {
        // Initialize selectedDate with today's date
        _selectedDateTime.value = Calendar.getInstance().timeInMillis
    }

    fun updateDate(year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = selectedDateTime.value ?: 0
        calendar.set(year, month, dayOfMonth)
        _selectedDateTime.value = calendar.timeInMillis
    }

    fun updateTime(hourOfDay: Int, minute: Int) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = selectedDateTime.value ?: 0
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)
        _selectedDateTime.value = calendar.timeInMillis
    }
}
