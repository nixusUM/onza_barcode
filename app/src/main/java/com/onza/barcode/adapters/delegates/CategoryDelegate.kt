package com.onza.barcode.adapters.delegates

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.onza.barcode.R
import com.onza.barcode.data.model.Category
import kotlinx.android.synthetic.main.item_category.view.*

/**
 * Created by Ilia Polozov on 23/February/2020
 */

class CategoryDelegate(context: Context): BaseDelegate<CategoryDelegate.ViewHolder, Category>(context) {

    override fun onBindViewHolder(position: Int, item: Category, holder: ViewHolder, payloads: MutableList<Any>) {
        holder.name.text = item.name
    }

    override fun isForViewType(item: Any?): Boolean = item is Category

    override fun onCreateViewHolder(layoutInflater: LayoutInflater, parent: ViewGroup?): ViewHolder {
        return ViewHolder(layoutInflater.inflate(
            R.layout.item_category,
            parent,
            false))
    }

    class ViewHolder(rootView: View): RecyclerView.ViewHolder(rootView) {
        val name: TextView = rootView.name
    }
}