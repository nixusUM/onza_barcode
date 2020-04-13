package com.onza.barcode.fragments

import com.onza.barcode.data.model.FavouritesResponse


/**
 * Created by Ilia Polozov on 12/March/2020
 */

interface BarCodeView {

    fun showError(text: String?)
    fun addScannedProduct(product: Any)
    fun showFavouriteView(products: List<FavouritesResponse>)
}