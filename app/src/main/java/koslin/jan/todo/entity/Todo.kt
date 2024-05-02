package koslin.jan.todo.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todos")
data class Todo(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val dueDate: Long,
    val createdAt: Long = System.currentTimeMillis(),
    val status: Status = Status.ACTIVE,
    val notification: Boolean = true
) {
    enum class Status {
        ACTIVE,
        COMPLETED
    }
}
