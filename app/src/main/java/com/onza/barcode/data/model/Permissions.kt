package com.onza.barcode.data.model

import java.io.Serializable

/**
 * Created by Ilia Polozov on 05/February/2020
 */

data class Permissions(val can_add_price: Boolean,
                       val can_add_review: Boolean,
                       val has_added_price: Boolean,
                       val has_added_review: Boolean,
                       val can_delete: Boolean): Serializable