package com.onza.barcode.data.model

/**
 * Created by Ilia Polozov on 10/April/2020
 */

data class ReviewsResponse(val data: List<Reviews>,
                           val total: Int,
                           val last_page: Int)