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

            var categoryImage = context.getDrawable(R.drawable.ic_zagushka)

            if (item.branch.category != null) {
                categoryImage = when (item.branch.category) {
                    "alkogolnie napitky" -> context.getDrawable(R.drawable.ic_alkogolnie_napitky)
                    "apteka" -> context.getDrawable(R.drawable.ic_apteka)
                    "bitovaya himia" -> context.getDrawable(R.drawable.ic_bitovaya_himia)
                    "gipermarkety" -> context.getDrawable(R.drawable.ic_gipermarkety)
                    "optika" -> context.getDrawable(R.drawable.ic_optika)
                    "prodovolstvennye magaziny" -> context.getDrawable(R.drawable.ic_prodovolstvennye_magaziny)
                    "supermarketi" -> context.getDrawable(R.drawable.ic_supermarketi)
                    else -> context.getDrawable(R.drawable.ic_zagushka)
                }
            }

            Glide.with(context)
                .load(categoryImage)
                .placeholder(R.drawable.ic_no_product_grey)
                .into(holder.shopImage)

        }
        holder.shopTitle.text = item.name
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