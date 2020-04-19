package com.onza.barcode.compare.products

import com.onza.barcode.data.model.Product

/**
 * Created by Ilia Polozov on 13/March/2020
 */

interface CompareProdutsView {

    fun showCompareProducts(compareList: List<Product>)
    fun showMessage(text: String)
    fun showPlaceHolderView()
}