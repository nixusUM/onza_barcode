package com.onza.barcode.data

import com.onza.barcode.data.model.Branch
import com.onza.barcode.data.model.Image
import java.io.Serializable

/**
 * Created by Ilia Polozov on 05/February/2020
 */

data class Price(val id: Int,
                 val product_id: Int,
                 val shop_id: Int,
                 val person_uid: Int,
                 val logo: Image?,
                 val name: String,
                 val price: Float,
                 val created_at: Long,
                 val updated_at: Long,
                 val branch: Branch): Serializable