package koslin.jan.todo.dialog

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activeTodosSwitch = view.findViewById(R.id.activeTodosSwitch)

        activeTodosSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                // Show all todos
                showToast("Showing active todos")
                todoViewModel.showActiveOnly(true)
            } else {
                // Hide completed todos
                showToast("Showing All todos")
                todoViewModel.showActiveOnly(false)
            }
        }

        val behavior = (dialog as BottomSheetDialog).behavior
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.skipCollapsed = true
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

}