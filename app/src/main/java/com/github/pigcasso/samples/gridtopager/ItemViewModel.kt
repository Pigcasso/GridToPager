package com.github.pigcasso.samples.gridtopager

import android.app.Application
import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ItemViewModel constructor(
    application: Application
) : AndroidViewModel(application) {

    private val _items = MutableLiveData<List<Item>>()
    val items: LiveData<List<Item>> = _items

    fun loadItems() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val items = queryItems()
                _items.postValue(items)
            }
        }
    }

    private fun queryItems(): List<Item> {
        val context: Context = getApplication()
        val items = mutableListOf<Item>()
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.ImageColumns._ID,
            MediaStore.Images.ImageColumns.DISPLAY_NAME,
            MediaStore.Images.ImageColumns.WIDTH,
            MediaStore.Images.ImageColumns.HEIGHT,
            MediaStore.Images.ImageColumns.ORIENTATION,
            MediaStore.Images.ImageColumns.DATA,
        )
        // val selection = "${MediaStore.Images.ImageColumns.DISPLAY_NAME} = ?"
        // val selectionArgs = arrayOf("20230807-120455.png")
        val selection = null
        val selectionArgs = null
        val sortOrder = "${MediaStore.Images.ImageColumns.DISPLAY_NAME} DESC"
        context.contentResolver.query(
            uri,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns._ID)
            val displayNameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DISPLAY_NAME)
            val widthColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.WIDTH)
            val heightColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.HEIGHT)
            val orientationColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.ORIENTATION)
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val displayName = cursor.getString(displayNameColumn)
                val width = cursor.getInt(widthColumn)
                val height = cursor.getInt(heightColumn)
                val orientation = cursor.getInt(orientationColumn)
                val data = cursor.getString(dataColumn)
                val contentUri = ContentUris.withAppendedId(uri, id)
                val item = Item(
                    rowId = id,
                    displayName = displayName,
                    width = width,
                    height = height,
                    orientation = orientation,
                    data = data,
                    contentUri = contentUri,
                )
                items.add(item)
            }
        }
        return items
    }
}