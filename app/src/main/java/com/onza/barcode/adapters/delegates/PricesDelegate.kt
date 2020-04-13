package com.onza.barcode.adapters.delegates

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.onza.barcode.R
import com.onza.barcode.data.Price
import kotlinx.android.synthetic.main.item_price.view.*

/**
 * Created by Ilia Polozov on 09/February/2020
 */

class PricesDelegate(context: Context, val callback: ItemClick)
    : BaseDelegate<PricesDelegate.ViewHolder, Price>(context) {

    override fun onBindViewHolder(position: Int, item: Price, holder: ViewHolder, payloads: MutableList<Any>) {
        holder.price.text = item.price.toString() + " Р"
        if (item.branch != null) {
            holder.address.text = item.branch.address
            holder.distance.text = item.branch.distance
        }
        holder.shopTitle.text = item.name

        if (item.logo != null) {
            Glide.with(context)
                .load(item.logo.url)
                .centerCrop()
                .placeholder(R.drawable.ic_no_product_grey)
                .into(holder.shopImage)
        }
    }

    override fun isForViewType(item: Any?): Boolean = item is Price

    override fun onCreateViewHolder(layoutInflater: LayoutInflater, parent: ViewGroup?): ViewHolder {
        return ViewHolder(layoutInflater.inflate(
            R.layout.item_price,
            parent,
            false))
    }

    class ViewHolder(rootView: View): RecyclerView.ViewHolder(rootView) {
        val rootView: ConstraintLayout = rootView.price_rootView
        val price: TextView = rootView.price
        val shopTitle: TextView = rootView.shop_title
        val address: TextView = rootView.address
        val shopImage: ImageView = rootView.imageView_shop
        val distance: TextView = rootView.distance
    }

    interface ItemClick {
        fun onItemClicked()
    }
}