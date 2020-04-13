package com.onza.barcode.data.model

import java.io.Serializable

/**
 * Created by Ilia Polozov on 05/March/2020
 */

data class CompareImages(val product_id: Int,
                         val image: String?): Serializable