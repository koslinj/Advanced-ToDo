package koslin.jan.todo.adapter

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import koslin.jan.todo.R
import koslin.jan.todo.entity.Todo
import java.io.File

class TempImageAdapter(private val context: Context, private var imagePaths: List<String>, private val onDeleted: (Int) -> Unit) :
    RecyclerView.Adapter<TempImageAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.temp_item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val filePath = imagePaths[position]

        Glide.with(holder.imageView)
            .load(File(filePath))
            .apply(RequestOptions().override(200, 200))
            .placeholder(R.drawable.spinner_icon) // Placeholder image while loading
            .error(R.drawable.delete_icon) // Error image if loading fails
            .into(holder.imageView)

        holder.deleteButton.setOnClickListener {
            imagePaths = imagePaths.toMutableList().apply { removeAt(position) }
            onDeleted(position)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return imagePaths.size
    }

    fun updateData(newData: List<String>) {
        imagePaths = newData
        notifyDataSetChanged()
    }

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)
    }
}

