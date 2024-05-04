package koslin.jan.todo.adapter

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import koslin.jan.todo.R

class ImageAdapter(private val context: Context, private val imageIds: List<Long>) :
    RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageId = imageIds[position]
        val mediaUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageId)

        Glide.with(holder.imageView)
            .load(mediaUri)
            .fitCenter()
            .placeholder(R.drawable.spinner_icon) // Placeholder image while loading
            .error(R.drawable.delete_icon) // Error image if loading fails
            .into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return imageIds.size
    }

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }
}
