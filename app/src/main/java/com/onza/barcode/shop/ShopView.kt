package com.onza.barcode.shop

import com.onza.barcode.data.model.Shop

/**
 * Created by Ilia Polozov on 13/March/2020
 */

interface ShopView {
    fun showShopList(shopList: List<Shop>)
    fun showMessage(text: String?)
}