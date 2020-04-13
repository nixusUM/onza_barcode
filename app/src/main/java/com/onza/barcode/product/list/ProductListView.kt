package com.onza.barcode.product.list

import com.onza.barcode.data.model.FavouritesResponse

/**
 * Created by Ilia Polozov on 13/March/2020
 */

interface ProductListView {

    fun showFavourites(favouriteList: List<Any>)
    fun showMessage(text: String)
    fun removedProduct(position: Int)
    fun updatedProduct()
    fun showFavouriteView(products: List<FavouritesResponse>)

}