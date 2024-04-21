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
        builder.setTitle("Delete Task")
        builder.setMessage("Are you sure you want to delete this Task?")
        builder.setPositiveButton(
            "Confirm"
        ) { dialog, which -> adapter.deleteItem(position) }
        builder.setNegativeButton(
            "Cancel"
        ) { dialog, which -> adapter.notifyItemChanged(viewHolder.adapterPosition) }
        val dialog = builder.create()
        dialog.show()

    }
}