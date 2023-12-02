package com.github.pigcasso.samples.gridtopager

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.SharedElementCallback
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.github.pigcasso.samples.gridtopager.GridActivity.Companion.EXTRA_CURRENT_POSITION
import com.github.pigcasso.samples.gridtopager.GridActivity.Companion.EXTRA_STARTING_POSITION
import com.github.pigcasso.samples.gridtopager.databinding.ActivityImagePagerBinding

class ImagePagerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImagePagerBinding
    private val viewModel: ItemViewModel by viewModels()

    private var startingPosition = 0
    private var currentPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityImagePagerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.background.setBackgroundColor(Config.VIEWER_BACKGROUND_COLOR)

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentPosition = position
            }
        })

        prepareSharedElementTransitions()
        postponeEnterTransition()

        startingPosition = intent.getIntExtra(EXTRA_STARTING_POSITION, 0)
        currentPosition = savedInstanceState?.getInt(EXTRA_CURRENT_POSITION) ?: startingPosition

        val adapter = ImagePagerAdapter(this, startingPosition)
        adapter.setListener(adapterListener)
        binding.viewPager.adapter = adapter
        viewModel.items.observe(this) {
            adapter.submitList(it)
            binding.viewPager.setCurrentItem(currentPosition, false)
        }
        viewModel.loadItems()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(EXTRA_CURRENT_POSITION, currentPosition)
    }

    override fun finishAfterTransition() {
        val data = Intent()
        data.putExtra(EXTRA_STARTING_POSITION, startingPosition)
        data.putExtra(EXTRA_CURRENT_POSITION, currentPosition)
        setResult(Activity.RESULT_OK, data)
        super.finishAfterTransition()
    }

    private fun prepareSharedElementTransitions() {
        val viewPager = binding.viewPager
        setEnterSharedElementCallback(object : SharedElementCallback() {
            override fun onMapSharedElements(
                names: MutableList<String>?,
                sharedElements: MutableMap<String, View>?
            ) {
                super.onMapSharedElements(names, sharedElements)
                val selectedViewHolder =
                    (viewPager[0] as RecyclerView).findViewHolderForAdapterPosition(currentPosition)
                        ?: return
                val view = selectedViewHolder.itemView
                sharedElements?.put(names!![0], view.findViewById(R.id.image))
            }
        })
    }

    private val adapterListener by lazy {
        object : ImagePagerAdapterListener {
            override fun onInit(viewHolder: RecyclerView.ViewHolder, position: Int) {
                /* no-op */
            }

            override fun onDrag(viewHolder: RecyclerView.ViewHolder, view: View, fraction: Float) {
                binding.background.updateBackgroundColor(
                    fraction,
                    Config.VIEWER_BACKGROUND_COLOR,
                    Color.TRANSPARENT
                )
            }

            override fun onRestore(
                viewHolder: RecyclerView.ViewHolder,
                view: View,
                fraction: Float
            ) {
                binding.background.changeToBackgroundColor(Config.VIEWER_BACKGROUND_COLOR)
            }

            override fun onRelease(viewHolder: RecyclerView.ViewHolder, view: View) {
                binding.background.changeToBackgroundColor(Color.TRANSPARENT)
                onBackPressed()
            }
        }
    }

    companion object {

        @JvmStatic
        fun makeIntent(context: Context, position: Int): Intent {
            return Intent(context, ImagePagerActivity::class.java)
                .putExtra(EXTRA_STARTING_POSITION, position)
        }
    }
}