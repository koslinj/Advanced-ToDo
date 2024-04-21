package koslin.jan.todo.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import koslin.jan.todo.entity.Todo

@Dao
interface TodoDao {
    @Insert
    suspend fun insert(todo: Todo)

    @Query("SELECT * FROM todos")
    suspend fun getAllTodos(): List<Todo>
}