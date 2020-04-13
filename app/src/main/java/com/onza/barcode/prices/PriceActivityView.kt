package com.onza.barcode.prices

import com.onza.barcode.data.Price

/**
 * Created by Ilia Polozov on 13/March/2020
 */

interface PriceActivityView {

    fun showProductPrices(priceList: List<Price>)
    fun showMessage(text: String?)
}