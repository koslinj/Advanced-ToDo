package koslin.jan.todo.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.Calendar

class DateViewModel : ViewModel() {
    private val _selectedDate = MutableLiveData<Long>()
    val selectedDate: LiveData<Long> get() = _selectedDate

    init {
        // Initialize selectedDate with today's date
        _selectedDate.value = Calendar.getInstance().timeInMillis
    }

    fun setSelectedDate(date: Long) {
        _selectedDate.value = date
    }
}
