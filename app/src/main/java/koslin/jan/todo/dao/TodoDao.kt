package koslin.jan.todo.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import koslin.jan.todo.entity.Attachment
import koslin.jan.todo.entity.Todo

@Dao
interface TodoDao {
    @Insert
    suspend fun insertAttachment(attachment: Attachment): Long

    @Query("SELECT * FROM attachments WHERE todoId = :todoId")
    fun getAttachmentsForTodo(todoId: Long): List<Attachment>

    @Insert
    suspend fun insert(todo: Todo): Long

    @Update
    suspend fun update(todo: Todo)

    @Query("SELECT * FROM todos WHERE id = :id")
    suspend fun getTodoById(id: Long): Todo?

    @Query("SELECT * FROM todos")
    suspend fun getAllTodos(): List<Todo>

    @Query("SELECT * FROM todos")
    fun getAllTodosLiveData(): LiveData<List<Todo>>

    @Query("SELECT * FROM todos WHERE category IN (:categories) AND status = :status")
    suspend fun getTodosByCategoriesAndStatus(categories: Set<String>, status: String): List<Todo>

    @Query("SELECT * FROM todos WHERE status = 'ACTIVE'")
    suspend fun getActiveTodos(): List<Todo>

    @Query("SELECT * FROM todos WHERE category IN (:categories)")
    suspend fun getTodosByCategories(categories: Set<String>): List<Todo>

    @Delete
    suspend fun delete(todo: Todo)

    @Query("UPDATE todos SET status = 'ACTIVE' WHERE id = :todoId")
    suspend fun setActive(todoId: Long)

    @Query("UPDATE todos SET status = 'COMPLETED' WHERE id = :todoId")
    suspend fun setCompleted(todoId: Long)

    @Query("UPDATE todos SET notification = false WHERE id = :todoId")
    suspend fun turnOffNotifications(todoId: Long)

    @Query("UPDATE todos SET notification = true WHERE id = :todoId")
    suspend fun turnOnNotifications(todoId: Long)
}