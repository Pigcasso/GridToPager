package com.github.pigcasso.samples.gridtopager

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.github.pigcasso.samples.gridtopager.databinding.FragmentImageBinding

class ImageFragment : Fragment() {

    private var _binding: FragmentImageBinding? = null
    private val binding get() = _binding!!

    private var listener: PhotoView2.Listener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is PhotoView2.Listener) {
            listener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentImageBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val item: Item = requireArguments().getParcelable(EXTRA_ITEM)!!

        binding.image.transitionName = item.imageTransitionName

        Glide.with(this)
            .load(item.contentUri)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    activity?.supportStartPostponedEnterTransition()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    activity?.supportStartPostponedEnterTransition()
                    return false
                }
            })
            .into(binding.image)

        binding.image.setListener(object : PhotoView2.Listener {
            override fun onDrag(view: PhotoView2, fraction: Float) {
                Log.d(TAG, "onDrag: ")
                listener?.onDrag(view, fraction)
            }

            override fun onRestore(view: PhotoView2, fraction: Float) {
                Log.d(TAG, "onRestore: ")
                listener?.onRestore(view, fraction)
            }

            override fun onRelease(view: PhotoView2) {
                Log.d(TAG, "onRelease: ")
                listener?.onRelease(view)
            }
        })
    }

    companion object {
        private const val TAG = "ImageFragment"
        private const val EXTRA_ITEM = "item"
        fun newInstance(item: Item): ImageFragment {
            val args = Bundle()
            args.putParcelable(EXTRA_ITEM, item)
            val fragment = ImageFragment()
            fragment.arguments = args
            return fragment
        }
    }
}