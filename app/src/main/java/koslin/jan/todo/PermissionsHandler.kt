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
        activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val allGranted = permissions.all { it.value }
            if (!allGranted) {
                Log.d("PERMISSION_REQUEST", "Some permissions were denied")
            } else {
                Log.d("PERMISSION_REQUEST", "All permissions were granted")
            }
        }

    fun handleAllPermissions() {
        requestClassicPermissions()

        if (alarmManager.canScheduleExactAlarms()) {
            // Set exact alarms.
            Toast.makeText(activity, activity.getString(R.string.alarm_reminder_working), Toast.LENGTH_SHORT).show()
        } else {
            showExplanationForExactAlarmPermission()
        }
    }

    private fun requestClassicPermissions() {
        val permissions = arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.POST_NOTIFICATIONS
        )

        val permissionsToRequest = mutableListOf<String>()

        for (permission in permissions) {
            if (checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission)
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            requestPermissionLauncher.launch(permissionsToRequest.toTypedArray())
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
            Log.d("PERMISSION_REQUEST", "Your device does not support this")
        }
    }

    private fun showExplanationForExactAlarmPermission() {
        AlertDialog.Builder(activity)
            .setTitle(activity.getString(R.string.permission_request_title))
            .setMessage(activity.getString(R.string.permission_request_message))
            .setPositiveButton(activity.getString(R.string.grant_permission)) { dialog, _ ->
                requestScheduleExactAlarmPermission()
                dialog.dismiss()
            }
            .setNegativeButton(activity.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}
