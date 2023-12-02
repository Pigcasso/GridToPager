package com.github.pigcasso.samples.gridtopager

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.github.pigcasso.samples.gridtopager.databinding.PagerItemPhotoBinding

class PhotoViewHolder(
    parent: ViewGroup,
    private val requestManager: RequestManager,
    private val callback: ImagePagerAdapterListener,
    private val binding: PagerItemPhotoBinding =
        PagerItemPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
) : RecyclerView.ViewHolder(binding.root) {

    init {
        binding.image.setListener(object : PhotoView2.Listener {
            override fun onDrag(view: PhotoView2, fraction: Float) {
                callback.onDrag(this@PhotoViewHolder, view, fraction)
            }

            override fun onRestore(view: PhotoView2, fraction: Float) {
                callback.onRestore(this@PhotoViewHolder, view, fraction)
            }

            override fun onRelease(view: PhotoView2) {
                callback.onRelease(this@PhotoViewHolder, view)
            }
        })
    }

    fun bind(position: Int, item: Item) {
        binding.image.transitionName = item.imageTransitionName

        requestManager.load(item.contentUri)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    callback.onInit(this@PhotoViewHolder, position)
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    callback.onInit(this@PhotoViewHolder, position)
                    return false
                }
            })
            .into(binding.image)
    }
}