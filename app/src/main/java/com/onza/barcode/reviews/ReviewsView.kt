package com.onza.barcode.reviews

import com.onza.barcode.data.model.Reviews

/**
 * Created by Ilia Polozov on 13/March/2020
 */

interface ReviewsView {

    fun showReviews(reviewsList: List<Reviews>)
    fun showMessage(text: String)
    fun removeReivew(positon: Int)
}