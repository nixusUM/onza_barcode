package com.onza.barcode.data.model

import java.io.Serializable

/**
 * Created by Ilia Polozov on 02/March/2020
 */

data class ProductCompareDetail(val id: String,
                                val title: String,
                                val values: List<Value>): Serializable