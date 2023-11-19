package com.github.pigcasso.samples.gridtopager

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver.OnPreDrawListener
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.SharedElementCallback
import com.github.pigcasso.samples.gridtopager.databinding.ActivityGridBinding

class GridActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGridBinding
    private val viewModel: ItemViewModel by viewModels()

    private var reenterState: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGridBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prepareTransitions()

        val adapter = GridAdapter(this)
        binding.recyclerView.adapter = adapter
        viewModel.items.observe(this) {
            adapter.submitList(it)
        }

        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        if (ActivityCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            viewModel.loadItems()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(permission), 1000)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1000) {
            var granted = true
            for (grantResult in grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    granted = false
                    break
                }
            }
            if (granted) {
                viewModel.loadItems()
            }
        }
    }

    override fun onActivityReenter(resultCode: Int, data: Intent?) {
        super.onActivityReenter(resultCode, data)

        reenterState = Bundle(data?.extras)
        val recyclerView = binding.recyclerView
        recyclerView.scrollToPosition(data?.getIntExtra(EXTRA_CURRENT_POSITION, 0) ?: 0)
        postponeEnterTransition()
        recyclerView.viewTreeObserver.addOnPreDrawListener(object : OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                recyclerView.viewTreeObserver.removeOnPreDrawListener(this)
                startPostponedEnterTransition()
                return true
            }
        })
    }

    private fun prepareTransitions() {
        val recyclerView = binding.recyclerView
        setExitSharedElementCallback(object : SharedElementCallback() {
            override fun onMapSharedElements(
                names: MutableList<String>?,
                sharedElements: MutableMap<String, View>?
            ) {
                super.onMapSharedElements(names, sharedElements)
                reenterState?.let {
                    val currentPosition = it.getInt(EXTRA_CURRENT_POSITION)
                    val selectedViewHolder =
                        recyclerView.findViewHolderForAdapterPosition(currentPosition) ?: return
                    sharedElements?.put(
                        names!![0],
                        selectedViewHolder.itemView.findViewById(R.id.imageview_item)
                    )
                }
                reenterState = null
            }
        })
    }

    companion object {
        const val EXTRA_STARTING_POSITION = "extra_starting_position"
        const val EXTRA_CURRENT_POSITION = "extra_current_position"
    }
}