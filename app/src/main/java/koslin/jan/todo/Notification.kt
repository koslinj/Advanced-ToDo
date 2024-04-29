package koslin.jan.todo

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.gson.Gson
import koslin.jan.todo.entity.Todo
import koslin.jan.todo.fragment.TodoDetailsFragment

class Notification : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val todoStr = intent?.getStringExtra(EXTRA_TODO) ?: ""
        val todo = Gson().fromJson(todoStr, Todo::class.java)
        if (todo != null && context != null) {
            // Create and show the notification
            createNotification(context, todo)
        }
    }

    private fun createNotification(context: Context, todo: Todo) {
        // Create a notification channel (for Android Oreo and higher)
        createNotificationChannel(context)

        // Create a notification
        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Todo Reminder")
            .setContentText("Reminder for: ${todo.title}")
            .setSmallIcon(R.drawable.time_icon)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        // Create an explicit intent for launching the app when the notification is clicked
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        notificationBuilder.setContentIntent(pendingIntent)

        // Show the notification
        val notificationManager = NotificationManagerCompat.from(context)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) return
        notificationManager.notify(todo.id.toInt(), notificationBuilder.build())
    }

    private fun createNotificationChannel(context: Context) {
        val name = "Todo Notification"
        val descriptionText = "Reminder for Todo items"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        private const val CHANNEL_ID = "TodoNotificationChannel"
        const val EXTRA_TODO = "todo_id"
    }
}
