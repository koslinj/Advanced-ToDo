package koslin.jan.todo

import android.Manifest
import android.app.AlarmManager
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.getSystemService
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.FragmentActivity

class PermissionsHandler(private val activity: FragmentActivity) {

    private val alarmManager: AlarmManager = activity.application.getSystemService<AlarmManager>()!!
    private val requestPermissionLauncher =
        activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                Log.d("POST_NOTIFICATION_PERMISSION", "USER DENIED PERMISSION")
            } else {
                Log.d("POST_NOTIFICATION_PERMISSION", "USER GRANTED PERMISSION")
            }
        }

    fun handleNotificationAndExactAlarm() {
        requestNotificationPermission()

        if (alarmManager.canScheduleExactAlarms()) {
            // Set exact alarms.
            Toast.makeText(activity, "ALARMY I PRZYPOMNIENIA DZIAŁAJĄ", Toast.LENGTH_SHORT).show()
        } else {
            showExplanationForExactAlarmPermission()
        }
    }

    private fun requestNotificationPermission() {
        val permission = Manifest.permission.POST_NOTIFICATIONS
        when {
            checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED -> {
                requestPermissionLauncher.launch(permission)
            }
        }
    }

    private fun requestScheduleExactAlarmPermission() {
        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
            data = Uri.parse("package:${activity.packageName}")
        }
        try {
            activity.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // Handle case where the device doesn't support ACTION_REQUEST_SCHEDULE_EXACT_ALARM action
            Toast.makeText(
                activity,
                "Twoje urządzenie nie obsługuje tej funkcji.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun showExplanationForExactAlarmPermission() {
        AlertDialog.Builder(activity)
            .setTitle("Prośba o uprawnienie")
            .setMessage("Aplikacja potrzebuje uprawnienia do planowania dokładnych alarmów w celu zapewnienia dokładnych powiadomień.")
            .setPositiveButton("Przyznaj uprawnienie") { dialog, _ ->
                requestScheduleExactAlarmPermission()
                dialog.dismiss()
            }
            .setNegativeButton("Anuluj") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}