package com.onza.barcode.data.model

/**
 * Created by Ilia Polozov on 28/February/2020
 */

data class ProductDetail(val id: Int,
                         val title: String,
                         val properties: List<ProductProperty>)