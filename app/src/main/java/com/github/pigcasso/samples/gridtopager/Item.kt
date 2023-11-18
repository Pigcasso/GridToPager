package com.github.pigcasso.samples.gridtopager

import android.net.Uri
import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import kotlinx.parcelize.Parcelize

@Parcelize
data class Item constructor(
    val rowId: Long,
    val displayName: String,
    val width: Int,
    val height: Int,
    val orientation: Int,
    val data: String,
    val contentUri: Uri,
) : Parcelable {
    val imageTransitionName: String
        get() = "image:$rowId"

    val displayNameTransitionName: String
        get() = "displayName:$rowId"

    companion object {
        val Callback = object : DiffUtil.ItemCallback<Item>() {
            override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
                return oldItem.rowId == newItem.rowId
            }

            override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
                return oldItem == newItem
            }
        }
    }
}
