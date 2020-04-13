package com.onza.barcode.data.model

import java.io.Serializable

/**
 * Created by Ilia Polozov on 28/January/2020
 */

data class Reviews(
    val id: Int,
    val rating: Float,
    val text: String,
    val positive_text: String,
    val negative_text: String,
    val shop_id: Int?,
    val product_id: Int,
    val person_uid: String?,
    val production_place: String,
    val created_at: String,
    val author: Author,
    val updated_at: Long
) : Serializable