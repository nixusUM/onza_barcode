package com.onza.barcode.data.model

import java.io.Serializable

/**
 * Created by Ilia Polozov on 05/February/2020
 */

data class ProductUpdates(val id: Int,
                          val rating: Float,
                          val avg_price: Float,
                          val amounts: Amounts,
                          val permissions: Permissions): Serializable