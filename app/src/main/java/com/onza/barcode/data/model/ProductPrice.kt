package com.onza.barcode.data.model

import java.io.Serializable

/**
 * Created by Ilia Polozov on 05/February/2020
 */

data class ProductPrice(val id: Int,
                        val price: Float,
                        val shop_id: Int,
                        val product_id: Int,
                        val updated_at: Long,
                        val created_at: Long,
                        val shop: Shop,
                        val permissions: Permissions): Serializable