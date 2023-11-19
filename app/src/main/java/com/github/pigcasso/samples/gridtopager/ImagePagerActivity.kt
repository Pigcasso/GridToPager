package com.github.pigcasso.samples.gridtopager

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.SharedElementCallback
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

        viewModel.items.observe(this) {
            binding.viewPager.adapter = ImagePagerAdapter(this, it)
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
                val currentFragment = supportFragmentManager
                    .findFragmentByTag("f${viewPager.currentItem}") ?: return
                val view = currentFragment.view ?: return
                sharedElements?.put(names!![0], view.findViewById(R.id.image))
            }
        })
    }

    companion object {

        @JvmStatic
        fun makeIntent(context: Context, position: Int): Intent {
            return Intent(context, ImagePagerActivity::class.java)
                .putExtra(EXTRA_STARTING_POSITION, position)
        }
    }
}