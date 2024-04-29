package koslin.jan.todo.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import koslin.jan.todo.entity.Todo

@Dao
interface TodoDao {
    @Insert
    suspend fun insert(todo: Todo)

    @Query("SELECT * FROM todos")
    suspend fun getAllTodos(): List<Todo>

    @Query("SELECT * FROM todos")
    fun getAllTodosLiveData(): LiveData<List<Todo>>

    @Delete
    suspend fun delete(todo: Todo)

    @Query("UPDATE todos SET status = 'ACTIVE' WHERE id = :todoId")
    suspend fun setActive(todoId: Long)

    @Query("UPDATE todos SET status = 'COMPLETED' WHERE id = :todoId")
    suspend fun setCompleted(todoId: Long)
}