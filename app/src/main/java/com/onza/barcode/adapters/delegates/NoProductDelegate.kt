package com.onza.barcode.adapters.delegates

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.onza.barcode.R
import com.onza.barcode.data.model.NoProduct
import kotlinx.android.synthetic.main.item_no_product.view.*

/**
 * Created by Ilia Polozov on 12/January/2020
 */

class NoProductDelegate(context: Context, val callback: ItemClick)
    : BaseDelegate<NoProductDelegate.ViewHolder, NoProduct>(context) {

    override fun onBindViewHolder(position: Int, item: NoProduct, holder: ViewHolder, payloads: MutableList<Any>) {
        holder.addProduct.setOnClickListener {
            callback.onAddProductClicked(position)
        }
    }

    override fun isForViewType(item: Any?): Boolean = item is NoProduct

    override fun onCreateViewHolder(layoutInflater: LayoutInflater, parent: ViewGroup?): ViewHolder {
        return ViewHolder(layoutInflater.inflate(
            R.layout.item_no_product,
            parent,
            false))
    }

    class ViewHolder(rootView: View): RecyclerView.ViewHolder(rootView) {
        val addProduct: ConstraintLayout = rootView.contraint_add_product
    }

    interface ItemClick {
        fun onAddProductClicked(position: Int)
    }
}