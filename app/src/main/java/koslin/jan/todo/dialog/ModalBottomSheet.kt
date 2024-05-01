package koslin.jan.todo.dialog

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.materialswitch.MaterialSwitch
import koslin.jan.todo.PreferencesFragment
import koslin.jan.todo.R
import koslin.jan.todo.viewmodel.TodoViewModel

class ModalBottomSheet : BottomSheetDialogFragment(R.layout.sheet) {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = requireContext().getSharedPreferences("todo_prefs", Context.MODE_PRIVATE)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val behavior = (dialog as BottomSheetDialog).behavior
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.skipCollapsed = true

        val preferencesFragmentContainer = view.findViewById<FragmentContainerView>(R.id.preferencesFragmentContainer)

        // Create and add the PreferencesFragment
        val preferencesFragment = PreferencesFragment()
        childFragmentManager.beginTransaction()
            .replace(preferencesFragmentContainer.id, preferencesFragment)
            .commit()
    }

}