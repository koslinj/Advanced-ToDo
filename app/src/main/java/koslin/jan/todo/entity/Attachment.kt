package koslin.jan.todo.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey

@Entity(tableName = "attachments", foreignKeys = [ForeignKey(entity = Todo::class, parentColumns = ["id"], childColumns = ["todoId"], onDelete = CASCADE)])
data class Attachment(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var todoId: Long,
    val uri: String
)
