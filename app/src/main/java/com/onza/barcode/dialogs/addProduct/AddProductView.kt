package com.onza.barcode.dialogs.addProduct

/**
 * Created by Ilia Polozov on 13/March/2020
 */

interface AddProductView {

    fun showMessage(text: String?)
    fun addedProduct(gtin: String)
}