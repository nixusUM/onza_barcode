package com.onza.barcode.data.model

import java.io.Serializable

/**
 * Created by Ilia Polozov on 06/January/2020
 */

data class Branch(val id: Int,
                  val distance: String,
                  val address: String?,
                  val category: String?,
                  val location: Location): Serializable