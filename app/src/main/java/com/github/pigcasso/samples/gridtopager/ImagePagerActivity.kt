package com.github.pigcasso.samples.gridtopager

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.SharedElementCallback
import com.github.pigcasso.samples.gridtopager.GridActivity.Companion.EXTRA_CURRENT_POSITION
import com.github.pigcasso.samples.gridtopager.databinding.ActivityImagePagerBinding

class ImagePagerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImagePagerBinding
    private val viewModel: ItemViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityImagePagerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prepareSharedElementTransitions()
        postponeEnterTransition()

        viewModel.items.observe(this) {
            binding.viewPager.adapter = ImagePagerAdapter(this, it)
            binding.viewPager.setCurrentItem(intent.getIntExtra(EXTRA_CURRENT_POSITION, 0), false)
        }
        viewModel.loadItems()
    }

    override fun finishAfterTransition() {
        val data = Intent()
        data.putExtra(EXTRA_CURRENT_POSITION, binding.viewPager.currentItem)
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
                .putExtra(EXTRA_CURRENT_POSITION, position)
        }
    }
}