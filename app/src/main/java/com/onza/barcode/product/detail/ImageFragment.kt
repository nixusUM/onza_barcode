package com.onza.barcode.product.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.onza.barcode.R
import kotlinx.android.synthetic.main.fragment_image.*

/**
 * Created by Ilia Polozov on 09/February/2020
 */

class ImageFragment: Fragment() {

    companion object {

        private val PHOTO_PATH: String = "extra-list"

        fun newInstance(list: String): ImageFragment {
            val fragment = ImageFragment()
            val bundle = Bundle()
            bundle.putString(PHOTO_PATH, list)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_image, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        Glide.with(this)
            .load(arguments!!.getString(PHOTO_PATH))
            .into(product_image)
    }
}