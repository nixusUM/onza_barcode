package com.onza.barcode.dialogs.addReview

import com.onza.barcode.data.model.Shop

/**
 * Created by Ilia Polozov on 13/March/2020
 */

interface AddReviewView {

    fun showMessage(text: String?)
    fun successAdded()
    fun initShop(shops: List<Shop>?)
}