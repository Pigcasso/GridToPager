package com.github.pigcasso.samples.gridtopager

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import java.util.concurrent.atomic.AtomicBoolean

class GridAdapter(
    private val activity: FragmentActivity
) : ListAdapter<Item, ItemViewHolder>(Item.Callback) {

    private val requestManager = Glide.with(activity)
    private val viewHolderListener = object : ViewHolderListener {
        private val enterTransitionStarted = AtomicBoolean(false)

        override fun onLoadCompleted(view: ImageView, adapterPosition: Int) {

        }

        override fun onItemClicked(view: View, adapterPosition: Int) {
            val intent = ImagePagerActivity.makeIntent(activity, adapterPosition)
            ActivityCompat.startActivity(activity, intent, null)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.grid_item, parent, false)
        return ItemViewHolder(view, requestManager, viewHolderListener)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.onBind(item)
    }
}

interface ViewHolderListener {
    fun onLoadCompleted(view: ImageView, adapterPosition: Int)

    fun onItemClicked(view: View, adapterPosition: Int)
}

class ItemViewHolder(
    itemView: View,
    private val requestManager: RequestManager,
    private val viewHolderListener: ViewHolderListener
) : RecyclerView.ViewHolder(itemView) {

    private val image = itemView.findViewById<ImageView>(R.id.imageview_item)

    init {
        itemView.setOnClickListener { viewHolderListener.onItemClicked(it, adapterPosition) }
    }

    fun onBind(item: Item) {
        image.transitionName = item.imageTransitionName

        requestManager
            .load(item.contentUri)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    viewHolderListener.onLoadCompleted(image, adapterPosition)
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    viewHolderListener.onLoadCompleted(image, adapterPosition)
                    return false
                }
            })
            .into(image)
    }
}