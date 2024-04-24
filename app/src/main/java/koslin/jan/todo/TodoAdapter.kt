package koslin.jan.todo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import koslin.jan.todo.entity.Todo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

class TodoAdapter(private var todoList: List<Todo>, private val onTodoDeleted: (Todo) -> Unit) : RecyclerView.Adapter<TodoAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val todoTextView: TextView = itemView.findViewById(R.id.todoTextView)
        val todoDateTextView: TextView = itemView.findViewById(R.id.todoDateTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.todo_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.todoTextView.text = todoList[position].title

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        holder.todoDateTextView.text = dateFormat.format(todoList[position].dueDate)
    }

    override fun getItemCount(): Int {
        return todoList.size
    }

    fun updateData(newData: List<Todo>) {
        todoList = newData
        notifyDataSetChanged()
    }

    fun deleteItem(position: Int) {
        onTodoDeleted(todoList[position])
    }
}
