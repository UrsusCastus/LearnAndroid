package imageviewerwebapi;

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.application.R
import com.example.application.task_15.CurrentImageActivityWebApi
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso

class LargeViewerWebApiAdapter(
    private val activity: Activity,
    private val itemLargeViewerArrayList: ArrayList<ImageData>,
    private val gridLayoutManager: GridLayoutManager
) : RecyclerView.Adapter<LargeViewerWebApiAdapter.LargeViewerWebApiViewHolder>() {

    companion object {
        const val NUMBER_OF_ITEM_TO_DISPLAY: Int = 3
        const val SPAN_COUNT_ONE_WEB_API: Int = 1
        const val SPAN_COUNT_THREE_WEB_API: Int = 3
        const val ITEM_TYPE_LIST: Int = 1
        const val ITEM_TYPE_GRID: Int = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LargeViewerWebApiViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.web_api_recycler_view_item_for_large_viewer, parent, false)
        setLayoutParams(parent, viewType, view)
        return LargeViewerWebApiViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemLargeViewerArrayList.size
    }

    override fun onBindViewHolder(holder: LargeViewerWebApiViewHolder, position: Int) {
        Picasso.get()
            .load(itemLargeViewerArrayList[position].url)
            .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
            .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
            .placeholder(R.drawable.ic_image_not_download)
            .error(R.drawable.ic_error)
            .fit()
            .centerCrop()
            .into(holder.imageView)

        holder.itemView.setOnClickListener {
            val intent = Intent(activity, CurrentImageActivityWebApi::class.java)
            intent.putExtra("getTitle", itemLargeViewerArrayList[position].title)
            intent.putExtra("getUrl", itemLargeViewerArrayList[position].url)
            activity.startActivity(intent)
        }
    }

    //вызывается перед очисткой внутренних данных ViewHolder
    override fun onViewRecycled(holder: LargeViewerWebApiViewHolder) {
        holder.imageClean()
    }

    override fun getItemViewType(position: Int): Int {
        val spanCount: Int = gridLayoutManager.spanCount
        if (spanCount == SPAN_COUNT_ONE_WEB_API) {
            return ITEM_TYPE_LIST
        } else {
            return ITEM_TYPE_GRID
        }
    }

    private fun setLayoutParams(parent: ViewGroup, viewType: Int, view: View) {
        val orientation: Int = parent.context.resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_PORTRAIT && viewType == ITEM_TYPE_LIST) {
            view.layoutParams.height = parent.height / NUMBER_OF_ITEM_TO_DISPLAY
            view.layoutParams.width = parent.width
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT && viewType == ITEM_TYPE_GRID) {
            view.layoutParams.height = parent.height / NUMBER_OF_ITEM_TO_DISPLAY
            view.layoutParams.width = parent.width / SPAN_COUNT_THREE_WEB_API
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE && viewType == ITEM_TYPE_LIST) {
            view.layoutParams.width = parent.width / NUMBER_OF_ITEM_TO_DISPLAY
            view.layoutParams.height = parent.height
        } else {
            view.layoutParams.width = parent.width / NUMBER_OF_ITEM_TO_DISPLAY
            view.layoutParams.height = parent.height / SPAN_COUNT_THREE_WEB_API
        }
    }

    class LargeViewerWebApiViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        //в Kotlin внешний класс не видит private члены своих вложенных классов.
        val imageView: ImageView = itemView.findViewById(R.id.web_api_vertical_image_view)

        internal fun imageClean() {
            Picasso.get()
                .cancelRequest(imageView)
            imageView.setImageDrawable(null)
        }
    }
}
