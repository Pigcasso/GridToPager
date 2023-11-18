package com.github.pigcasso.samples.gridtopager

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.github.pigcasso.samples.gridtopager.databinding.ActivityImagePagerBinding

class ImagePagerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImagePagerBinding
    private val viewModel: ItemViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityImagePagerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.items.observe(this) {
            binding.viewPager.adapter = ImagePagerAdapter(this, it)
            binding.viewPager.currentItem = intent.getIntExtra(EXTRA_POSITION, 0)
        }
        viewModel.loadItems()
    }

    companion object {
        private const val EXTRA_POSITION = "position"

        @JvmStatic
        fun makeIntent(context: Context, position: Int): Intent {
            return Intent(context, ImagePagerActivity::class.java)
                .putExtra(EXTRA_POSITION, position)
        }
    }
}