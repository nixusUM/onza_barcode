package com.onza.barcode.product.fragments

import com.onza.barcode.data.model.ProductDetail

/**
 * Created by Ilia Polozov on 13/March/2020
 */

interface AboutFragmentView {

    fun showError(text: String?)
    fun showAboutProduct(productDetail: List<ProductDetail>)
}