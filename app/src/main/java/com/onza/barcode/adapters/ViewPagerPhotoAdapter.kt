package com.onza.barcode.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.onza.barcode.product.detail.ImageFragment

/**
 * Created by Ilia Polozov on 09/February/2020
 */

class ViewPagerPhotoAdapter(fm: FragmentManager?, list: ArrayList<String>) : FragmentStatePagerAdapter(fm!!) {

    var mUrls: ArrayList<String> = list

    override fun getItem(position: Int): Fragment = ImageFragment.newInstance(mUrls[position])

    override fun getCount(): Int = mUrls.size
}