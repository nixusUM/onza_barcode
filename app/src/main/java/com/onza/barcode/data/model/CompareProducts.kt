package com.onza.barcode.data.model

/**
 * Created by Ilia Polozov on 02/March/2020
 */

data class CompareProducts(val products: List<Product>,
                           val details: List<CompareProperty>)