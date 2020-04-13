package com.onza.barcode.data.model

import java.io.Serializable

/**
 * Created by Ilia Polozov on 02/March/2020
 */

data class CompareProperty(val id: Int,
                           val title: String,
                           val properties: List<ProductCompareDetail>): Serializable