package com.onza.barcode.data.model

import java.io.Serializable

/**
 * Created by Ilia Polozov on 06/January/2020
 */

data class Shop(val id: Int,
                val name: String,
                val logo: String?,
                val branch: Branch,
                val external_id: Long,
                val category: String?,
                val price: Float): Serializable