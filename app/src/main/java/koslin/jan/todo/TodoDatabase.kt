package koslin.jan.todo

import androidx.room.Database
import androidx.room.RoomDatabase
import koslin.jan.todo.dao.TodoDao
import koslin.jan.todo.entity.Attachment
import koslin.jan.todo.entity.Todo

@Database(entities = [Todo::class, Attachment::class], version = 1)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
}
