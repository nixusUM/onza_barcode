package com.onza.barcode.product.fragments.detail

import com.onza.barcode.data.model.Product
import com.onza.barcode.data.model.Reviews

/**
 * Created by Ilia Polozov on 13/March/2020
 */

interface DetailFragmentView {

    fun showError(text: String?)
    fun showReviwes(reviews: List<Reviews>, count: Int)
    fun initProductData(product: Product)
}