package com.onza.barcode.compare

import com.onza.barcode.data.model.CompareCategories

/**
 * Created by Ilia Polozov on 13/March/2020
 */

interface CompareView {

    fun showCompareCategories(compareCategoryList: List<CompareCategories>)
    fun showMessage(text: String)
    fun removedCompareCategory(position: Int)

}