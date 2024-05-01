package koslin.jan.todo.dialog

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.materialswitch.MaterialSwitch
import koslin.jan.todo.R
import koslin.jan.todo.viewmodel.TodoViewModel

class ModalBottomSheet : BottomSheetDialogFragment(R.layout.sheet) {
    private val todoViewModel: TodoViewModel by activityViewModels()
    private lateinit var activeTodosSwitch: MaterialSwitch
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = requireContext().getSharedPreferences("todo_prefs", Context.MODE_PRIVATE)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activeTodosSwitch = view.findViewById(R.id.activeTodosSwitch)

        val showActiveTodos = sharedPreferences.getBoolean("show_active_todos", false)
        activeTodosSwitch.isChecked = showActiveTodos

        activeTodosSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            sharedPreferences.edit().putBoolean("show_active_todos", isChecked).apply()

            showToast(if (isChecked) "ACTIVE ONLY" else "ALL")
            todoViewModel.showActiveOnly(isChecked)
        }

        val behavior = (dialog as BottomSheetDialog).behavior
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.skipCollapsed = true
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

}