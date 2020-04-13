package com.onza.barcode.adapters.delegates

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.onza.barcode.R
import com.onza.barcode.data.model.CompareCategories
import kotlinx.android.synthetic.main.item_compare_in_list.view.*

/**
 * Created by Ilia Polozov on 01/March/2020
 */

class CompareListDelegate(context: Context, val callback: ItemClick)
    : BaseDelegate<CompareListDelegate.ViewHolder, CompareCategories>(context) {

    override fun onBindViewHolder(position: Int, item: CompareCategories, holder: ViewHolder, payloads: MutableList<Any>) {
        holder.name.text = item.name
        holder.count.text = item.count.toString()
        holder.rogFG.setOnClickListener {
            callback.onCompareClicked(item.category_id, item.name)
        }
    }

    override fun isForViewType(item: Any?): Boolean = item is CompareCategories

    override fun onCreateViewHolder(layoutInflater: LayoutInflater, parent: ViewGroup?): ViewHolder {
        return ViewHolder(layoutInflater.inflate(
            R.layout.item_compare_in_list,
            parent,
            false))
    }

    class ViewHolder(rootView: View): RecyclerView.ViewHolder(rootView) {
        val name: TextView = rootView.name
        val parentView: ConstraintLayout = rootView.parentView
        val count: TextView = rootView.count
        val rogFG: LinearLayout = rootView.rowFG
    }

    interface ItemClick {
        fun onCompareClicked(id: Int, name: String)
    }
}