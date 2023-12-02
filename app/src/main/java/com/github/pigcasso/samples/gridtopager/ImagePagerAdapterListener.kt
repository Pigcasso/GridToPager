package com.github.pigcasso.samples.gridtopager

import android.view.View
import androidx.recyclerview.widget.RecyclerView

interface ImagePagerAdapterListener {
    fun onInit(viewHolder: RecyclerView.ViewHolder, position: Int)
    fun onDrag(viewHolder: RecyclerView.ViewHolder, view: View, fraction: Float)
    fun onRestore(viewHolder: RecyclerView.ViewHolder, view: View, fraction: Float)
    fun onRelease(viewHolder: RecyclerView.ViewHolder, view: View)
}