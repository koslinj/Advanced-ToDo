package koslin.jan.todo

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.preference.MultiSelectListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import koslin.jan.todo.config.Keys
import koslin.jan.todo.viewmodel.TodoViewModel

class PreferencesFragment : PreferenceFragmentCompat() {
    private val todoViewModel: TodoViewModel by activityViewModels()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        findPreference<SwitchPreferenceCompat>(Keys.VISIBILITY_KEY)
            ?.setOnPreferenceChangeListener { _, newValue ->
                newValue as Boolean
                todoViewModel.showActiveOnly(newValue)
                true
            }

        findPreference<MultiSelectListPreference>(Keys.CATEGORIES_KEY)
            ?.setOnPreferenceChangeListener { _, newValue ->
                val selectedCategories = newValue as HashSet<String>
                todoViewModel.fetchTodosByCategories(selectedCategories)
                true
            }

    }

}
