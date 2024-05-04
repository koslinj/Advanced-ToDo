package koslin.jan.todo.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import koslin.jan.todo.R
import koslin.jan.todo.entity.Todo
import java.text.SimpleDateFormat
import java.util.Locale

class TodoAdapter(
    private var todoList: List<Todo>,
    private val onTodoDeleted: (Todo) -> Unit,
    private val onTodoClicked: (Todo) -> Unit,
    private val onToggleClicked: (Todo) -> Unit,
) : RecyclerView.Adapter<TodoAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val todoTextView: TextView = itemView.findViewById(R.id.todoTextView)
        val todoDateTextView: TextView = itemView.findViewById(R.id.todoDateTextView)
        val completeButton: ImageButton = itemView.findViewById(R.id.completeButton)
        val hasAttachmentsIcon: ImageView = itemView.findViewById(R.id.hasAttachmentsIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.todo_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val myTodo = todoList[position]

        if(myTodo.attachments.isNotEmpty()){
            holder.hasAttachmentsIcon.visibility = ImageView.VISIBLE
        } else {
            holder.hasAttachmentsIcon.visibility = ImageView.INVISIBLE
        }

        if(myTodo.status === Todo.Status.ACTIVE) {
            holder.completeButton.setImageResource(R.drawable.circle_icon)
            holder.completeButton.setBackgroundResource(R.drawable.image_button_bg_unchecked)
        } else {
            holder.completeButton.setImageResource(R.drawable.check_circle_icon)
            holder.completeButton.setBackgroundResource(R.drawable.image_button_bg_checked)
        }

        holder.todoTextView.text = todoList[position].title

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        holder.todoDateTextView.text = dateFormat.format(todoList[position].dueDate)

        holder.completeButton.setOnClickListener {
            onToggleClicked(todoList[position])
        }

        // Set click listener
        holder.itemView.setOnClickListener {
            onTodoClicked(todoList[position])
        }
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
