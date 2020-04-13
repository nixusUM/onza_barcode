package com.onza.barcode.category

import com.onza.barcode.data.model.Category

/**
 * Created by Ilia Polozov on 13/March/2020
 */

interface CategoryListView {

    fun showCategoryList(categoryList: List<Category>)
    fun showMessage(text: String?)
}