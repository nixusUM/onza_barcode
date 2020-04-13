package com.onza.barcode.adapters.delegates

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.onza.barcode.R
import com.onza.barcode.data.model.Category
import kotlinx.android.synthetic.main.item_all_categories.view.*

/**
 * Created by Ilia Polozov on 26/February/2020
 */

class CategoryInListDelegate(context: Context, val callBack: Callback): BaseDelegate<CategoryInListDelegate.ViewHolder, Category>(context) {

    override fun onBindViewHolder(position: Int, item: Category, holder: ViewHolder, payloads: MutableList<Any>) {
        holder.name.text = item.name
        holder.itemLayout.setOnClickListener {
            callBack.onCategoryClicked(item.name, item.id)
        }
    }

    override fun isForViewType(item: Any?): Boolean = item is Category

    override fun onCreateViewHolder(layoutInflater: LayoutInflater, parent: ViewGroup?): ViewHolder {
        return ViewHolder(layoutInflater.inflate(
            R.layout.item_all_categories,
            parent,
            false))
    }

    class ViewHolder(rootView: View): RecyclerView.ViewHolder(rootView) {
        val name: TextView = rootView.name
        val itemLayout: LinearLayout = rootView.parentView
    }

    interface Callback {
        fun onCategoryClicked(name: String, id: Int)
    }
}