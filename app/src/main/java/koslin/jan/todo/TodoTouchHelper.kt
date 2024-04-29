package koslin.jan.todo


import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView


class TodoTouchHelper(private val context: Context, adapter: TodoAdapter) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
    private val adapter: TodoAdapter

    init {
        this.adapter = adapter
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        val builder = AlertDialog.Builder(context)
        builder.setTitle(context.getString(R.string.delete_task))
        builder.setMessage(context.getString(R.string.delete_task_question))
        builder.setPositiveButton(
            context.getString(R.string.confirm)
        ) { dialog, which -> adapter.deleteItem(position) }
        builder.setNegativeButton(
            context.getString(R.string.cancel)
        ) { dialog, which -> adapter.notifyItemChanged(viewHolder.adapterPosition) }
        builder.setOnCancelListener { dialog -> adapter.notifyItemChanged(viewHolder.adapterPosition) }
        val dialog = builder.create()
        dialog.show()

    }
}