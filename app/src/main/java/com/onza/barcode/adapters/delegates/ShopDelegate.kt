package com.onza.barcode.adapters.delegates

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.onza.barcode.R
import com.onza.barcode.data.model.Shop
import kotlinx.android.synthetic.main.item_shop.view.*

/**
 * Created by Ilia Polozov on 24/January/2020
 */

class ShopDelegate(context: Context, val callback: ItemClick)
    : BaseDelegate<ShopDelegate.ViewHolder, Shop>(context) {

    private var selectedPosition = -1

    override fun onBindViewHolder(position: Int, item: Shop, holder: ViewHolder, payloads: MutableList<Any>) {
        holder.title.text = item.name
        holder.distance.text = "~ " + item.branch.distance

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
            .into(holder.imgShop)

        if (item.isSelected) {
            holder.cardViewShop.setCardBackgroundColor(Color.parseColor("#26CB5A"))
            holder.title.setTextColor(Color.WHITE)
            holder.distance.setTextColor(Color.WHITE)
        } else {
            holder.cardViewShop.setCardBackgroundColor(Color.WHITE)
            holder.title.setTextColor(Color.BLACK)
            holder.distance.setTextColor(Color.parseColor("#2A83FF"))
        }

        holder.cardViewShop.setOnClickListener {
            item.isSelected = true
            callback.shopSelected(item, holder.adapterPosition)
        }
    }

    override fun isForViewType(item: Any?): Boolean = item is Shop

    fun updateItem(position: Int) {
        this.selectedPosition = position
    }

    override fun onCreateViewHolder(layoutInflater: LayoutInflater, parent: ViewGroup?): ViewHolder {
        return ViewHolder(layoutInflater.inflate(
            R.layout.item_shop,
            parent,
            false))
    }

    class ViewHolder(rootView: View): RecyclerView.ViewHolder(rootView) {
        val cardViewShop: CardView = rootView.cardView_shop
        val title: TextView = rootView.title
        val distance: TextView = rootView.distance
        val imgShop: ImageView = rootView.img_shop

    }

    interface ItemClick {
        fun shopSelected(shop: Shop, position: Int)
    }
}