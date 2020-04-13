package com.onza.barcode.data.model.ongoing

/**
 * Created by Ilia Polozov on 21/February/2020
 */

data class Review(val rating: Double,
                  val shop_branch_id: Int,
                  val text: String,
                  val positive_text: String,
                  val negative_text: String)