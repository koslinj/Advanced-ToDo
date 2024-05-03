package koslin.jan.todo.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "todos")
data class Todo(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val category: String,
    val dueDate: Long,
    val createdAt: Long = System.currentTimeMillis(),
    val status: Status = Status.ACTIVE,
    val notification: Boolean = true
) {
    @Ignore
    var attachments: List<Attachment> = listOf() // List of attachments for the to_do

    enum class Status {
        ACTIVE,
        COMPLETED
    }
}
