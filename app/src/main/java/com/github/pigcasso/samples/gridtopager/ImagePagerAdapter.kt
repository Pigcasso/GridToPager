package com.github.pigcasso.samples.gridtopager

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.util.concurrent.atomic.AtomicBoolean

class ImagePagerAdapter(
    private val activity: FragmentActivity,
    private val initialPosition: Int,
) : ListAdapter<Item, PhotoViewHolder>(Item.Callback) {

    private val requestManager = Glide.with(activity)
    private var listener: ImagePagerAdapterListener? = null

    fun setListener(callback: ImagePagerAdapterListener?) {
        listener = callback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        return PhotoViewHolder(parent, requestManager, callback)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(position, item)
    }

    private val callback: ImagePagerAdapterListener = object : ImagePagerAdapterListener {
        private val enterTransitionStarted = AtomicBoolean(false)

        override fun onInit(viewHolder: RecyclerView.ViewHolder, position: Int) {
            if (initialPosition != position) {
                return
            }
            if (enterTransitionStarted.getAndSet(true)) {
                return
            }
            activity.startPostponedEnterTransition()
            listener?.onInit(viewHolder, position)
        }

        override fun onDrag(viewHolder: RecyclerView.ViewHolder, view: View, fraction: Float) {
            listener?.onDrag(viewHolder, view, fraction)
        }

        override fun onRestore(viewHolder: RecyclerView.ViewHolder, view: View, fraction: Float) {
            listener?.onRestore(viewHolder, view, fraction)
        }

        override fun onRelease(viewHolder: RecyclerView.ViewHolder, view: View) {
            listener?.onRelease(viewHolder, view)
        }
    }
}