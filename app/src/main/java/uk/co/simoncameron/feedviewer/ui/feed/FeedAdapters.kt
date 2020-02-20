package uk.co.simoncameron.feedviewer.ui.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Observable
import kotlinx.android.synthetic.main.imagewidget_view.view.*
import kotlinx.android.synthetic.main.sliiderwidget_view.view.*
import uk.co.simoncameron.feedviewer.R
import uk.co.simoncameron.feedviewer.domain.pojo.FeedItem
import uk.co.simoncameron.feedviewer.domain.pojo.Image
import uk.co.simoncameron.feedviewer.domain.pojo.ImageItem
import uk.co.simoncameron.feedviewer.domain.pojo.SliiderItem
import uk.co.simoncameron.feedviewer.utils.load
import java.util.concurrent.TimeUnit


class FeedAdapter(private val onClick: (String) -> Unit): RecyclerView.Adapter<ViewHolder<*>>(){

    private var data = listOf<FeedItem>()

    fun setData(data: List<FeedItem>){
        this.data = data
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int) =
        when(data[position]) {
            is ImageItem -> ViewType.IMAGE_WIDGET
            is SliiderItem -> ViewType.SLIIDER_WIDGET
        }.toInt()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        when(ViewType.fromInt(viewType)) {
            ViewType.IMAGE_WIDGET -> ImageWidgetViewHolder(parent, onClick)
            ViewType.SLIIDER_WIDGET -> SliiderWidgetViewHolder(parent, onClick)
        }



    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder<*>, position: Int) {
        val item = data[position]
        if(holder is ImageWidgetViewHolder && item is ImageItem) holder.onBind(item)
        if(holder is SliiderWidgetViewHolder && item is SliiderItem) holder.onBind(item)
    }

    private enum class ViewType(private val intValue: Int) {
        IMAGE_WIDGET(0),
        SLIIDER_WIDGET(1);

        fun toInt() = intValue

        companion object {
            fun fromInt(value: Int): ViewType =
                values().firstOrNull { it.intValue == value }
                    ?: throw NoSuchElementException("Unknown FeedAdapter.ViewType enum for $value")
        }
    }
}

class SliiderAdapter(private val onClick: (String) -> Unit): RecyclerView.Adapter<ImageWidgetViewHolder>() {

    private var data = listOf<ImageItem>()

    fun setData(data: List<ImageItem>){
        this.data = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ImageWidgetViewHolder(parent, onClick)

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ImageWidgetViewHolder, position: Int) =
        holder.onBind(data[position])

}

sealed class ViewHolder<T : FeedItem>(parent: ViewGroup, @LayoutRes layoutRes: Int):
    RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)) {

    abstract fun onBind(item: T)
}

class ImageWidgetViewHolder(parent: ViewGroup, private val onClick: (String) -> Unit)
    : ViewHolder<ImageItem>(parent, R.layout.imagewidget_view) {

    override fun onBind(item: ImageItem) {
        itemView.apply {
            imagewidget_imageslayout.addImages(item.images)
            imagewidget_title.text = item.title
            setOnClickListener { onClick(item.deepLink) }
        }
    }

    private fun LinearLayout.addImages(images: List<Image>) {
        // a proper solution should be adding views to the constraint layout.
        // I was having some problems and didn't have time to dwell on it!

        removeAllViews()

        images.forEach { imageItem ->
            ImageView(context).let {
                it.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT).apply {
                    weight = 1f
                }
                imagewidget_imageslayout.addView(it)
                it.load(imageItem.url)
            }
        }
    }
}

class SliiderWidgetViewHolder(parent: ViewGroup, private val onClick: (String) -> Unit)
    : ViewHolder<SliiderItem>(parent, R.layout.sliiderwidget_view) {

    private val adapter by lazy {
        SliiderAdapter(onClick)
    }

    init {
        itemView.sliider_list.let {
            it.adapter = adapter
            PagerSnapHelper().attachToRecyclerView(it)
        }
    }

    override fun onBind(item: SliiderItem) {
        adapter.setData(item.images)

        Observable.interval(3, TimeUnit.SECONDS)
            .take(item.images.size.toLong() - 1)
            .scan(0) { previous, _  -> previous + 1 }
            .doOnNext { itemView.sliider_list?.smoothScrollToPosition(it) }
            .subscribe()
    }
}