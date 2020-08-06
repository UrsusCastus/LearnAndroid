package imageviewerwebapi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.application.R
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso

class SmallViewerWebApiAdapter(
    private val itemSmallViewerArrayList: ArrayList<ImageData>
) : RecyclerView.Adapter<SmallViewerWebApiAdapter.SmallViewerWebApiViewHolder>() {

    private var itemPositionListener: ItemPositionListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SmallViewerWebApiViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.web_api_recycler_view_item_for_small_viewr, parent, false)
        return SmallViewerWebApiViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemSmallViewerArrayList.size
    }

    override fun onBindViewHolder(holder: SmallViewerWebApiViewHolder, position: Int) {
        Picasso.get()
            .load(itemSmallViewerArrayList[position].url)
            .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
            .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
            .placeholder(R.drawable.ic_image_not_download)
            .error(R.drawable.ic_error)
            .fit()
            .centerCrop()
            .into(holder.imageView)

        holder.imageView.setOnClickListener {
            itemPositionListener?.onItemClicked(position)
        }
    }

    override fun onViewRecycled(holder: SmallViewerWebApiViewHolder) {
        holder.imageClean()
    }

    fun setSelectPositionListener(callbackListener: ItemPositionListener) {
        itemPositionListener = callbackListener
    }

    class SmallViewerWebApiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.web_api_horizontal_image_view)

        internal fun imageClean() {
            Picasso.get()
                .cancelRequest(imageView)
            imageView.setImageDrawable(null)
        }
    }
}
