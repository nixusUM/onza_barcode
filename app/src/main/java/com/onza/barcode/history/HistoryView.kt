package com.onza.barcode.history

import com.onza.barcode.data.model.FavouriteProducts
import com.onza.barcode.data.model.FavouritesResponse

/**
 * Created by Ilia Polozov on 13/March/2020
 */

interface HistoryView {

    fun showProductHistory(historyList: List<Any>)
    fun showMessage(text: String)
    fun showFavouriteView(products: List<FavouritesResponse>)

}