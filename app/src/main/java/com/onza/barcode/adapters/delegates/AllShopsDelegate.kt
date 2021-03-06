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
import com.onza.barcode.data.model.Shop
import kotlinx.android.synthetic.main.item_all_shop.view.*

/**
 * Created by Ilia Polozov on 24/January/2020
 */

class AllShopsDelegate(context: Context, val callback: ItemClick)
    : BaseDelegate<AllShopsDelegate.ViewHolder, Shop>(context) {

    override fun onBindViewHolder(position: Int, item: Shop, holder: ViewHolder, payloads: MutableList<Any>) {
        holder.title.text = item.name
        if (item.branch.address != null) {
            holder.address.text = item.branch.address
        }
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

        holder.parentVIew.setOnClickListener {
            callback.onShopClicked(item, holder.adapterPosition)
        }
    }

    override fun isForViewType(item: Any?): Boolean = item is Shop

    override fun onCreateViewHolder(layoutInflater: LayoutInflater, parent: ViewGroup?): ViewHolder {
        return ViewHolder(layoutInflater.inflate(
            R.layout.item_all_shop,
            parent,
            false))
    }

    class ViewHolder(rootView: View): RecyclerView.ViewHolder(rootView) {
        val title: TextView = rootView.title
        val distance: TextView = rootView.distance
        val address: TextView = rootView.address
        val shopImage: ImageView = rootView.imageView_shop
        val parentVIew: ConstraintLayout = rootView.parentView
    }

    interface ItemClick {
        fun onShopClicked(shop: Shop, position: Int)
    }
}