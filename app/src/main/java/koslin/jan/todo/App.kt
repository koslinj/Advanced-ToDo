package koslin.jan.todo

import android.app.Application
import androidx.room.Room

class App : Application() {

    companion object {
        lateinit var database: TodoDatabase
    }

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            applicationContext,
            TodoDatabase::class.java,
            "todo_db"
        ).build()
    }
}