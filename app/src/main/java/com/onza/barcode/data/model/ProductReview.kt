package com.onza.barcode.data.model

import java.io.Serializable

/**
 * Created by Ilia Polozov on 05/February/2020
 */

data class ProductReview(val id: Int,
                         val rating: Float,
                         val text: String,
                         val positive_text: String,
                         val negative_text: String,
                         val shop_id: Int,
                         val product_id: Int,
                         val person_uid: String,
                         val created_at: Long,
                         val updated_at: Long,
                         val author: Author,
                         val permissions: Permissions): Serializable