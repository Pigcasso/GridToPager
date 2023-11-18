package com.github.pigcasso.samples.gridtopager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ImagePagerAdapter(
    activity: FragmentActivity,
    private val items: List<Item>
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int {
        return items.size
    }

    override fun createFragment(position: Int): Fragment {
        return ImageFragment.newInstance(items[position])
    }
}