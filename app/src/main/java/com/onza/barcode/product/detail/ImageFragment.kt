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

        private val EXTRA_LIST: String = "extra-list"

        fun newInstance(list: ArrayList<String>): ImageFragment {
            val fragment = ImageFragment()
            val bundle = Bundle()
            bundle.putStringArrayList(EXTRA_LIST, list)
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
        for (item in arguments!!.getStringArrayList(EXTRA_LIST)) {
            Glide.with(this)
                .load(item)
                .into(product_image)
        }
    }
}