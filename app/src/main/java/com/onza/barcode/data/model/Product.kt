package com.onza.barcode.data.model

import java.io.Serializable

/**
 * Created by Ilia Polozov on 06/January/2020
 */

data class Product(val id: Int,
                   val name: String?,
                   val gtin: String?,
                   val category: Category?,
                   val provider: Provider?,
                   val shops: List<Shop>?,
                   val description: String?,
                   val amounts: Amounts?,
                   val avg_price: Float?,
                   val production_place: String?,
                   val reviews: List<Reviews>?,
                   val permissions: Permissions?,
                   var detail: List<CompareProperty>?,
                   val rating: Double,
                   val lists: Lists?,
                   val image: Image?,
                   val compare_images: List<CompareImages>?,
                   val images: ArrayList<Image>?): Serializable