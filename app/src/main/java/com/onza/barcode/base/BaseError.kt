package com.onza.barcode.base

import java.io.Serializable

/**
 * Created by Ilia Polozov on 04/January/2020
 */

data class BaseError(val description: String,
                     val title: String,
                     val displaying_type: String): Serializable