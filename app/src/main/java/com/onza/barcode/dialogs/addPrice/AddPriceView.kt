package com.onza.barcode.dialogs.addPrice

import com.onza.barcode.data.model.ProductPrice
import com.onza.barcode.data.model.Shop

/**
 * Created by Ilia Polozov on 13/March/2020
 */

interface AddPriceView {

    fun showMessage(text: String?)
    fun successAdded(productPrice: ProductPrice)
    fun initShopList(shops: List<Shop>?)
}