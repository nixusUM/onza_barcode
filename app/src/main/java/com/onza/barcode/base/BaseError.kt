package com.onza.barcode.base

/**
 * Created by Ilia Polozov on 04/January/2020
 */

data class BaseError(val description: String,
                     val fields: Map<String, String>)