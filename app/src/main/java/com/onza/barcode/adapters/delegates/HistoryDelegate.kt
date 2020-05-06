package com.onza.barcode.adapters.delegates

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.onza.barcode.R
import com.onza.barcode.data.model.HistoryData
import com.onza.barcode.data.model.Product
import com.ymb.ratingbar_lib.RatingBar
import kotlinx.android.synthetic.main.item_scan_history.view.*

/**
 * Created by Ilia Polozov on 25/January/2020
 */

class HistoryDelegate(context: Context, val callback: ItemClick, val productCallBack: ProductClick)
    : BaseDelegate<HistoryDelegate.ViewHolder, HistoryData>(context) {

    override fun onBindViewHolder(position: Int, item: HistoryData, holder: ViewHolder, payloads: MutableList<Any>) {
        holder.name.text = item.product.name
        holder.address.text = item.product.production_place
        holder.rating.rating = item.product.rating.toFloat()
        holder.ratingCount.text = item.product.rating.toString()
        holder.price.text =  "~ " + String.format("%.2f", item.product.avg_price) + " Р"

        if (!item.product.images.isNullOrEmpty()) {
            Glide.with(context)
                .load(item.product.images[0].url)
                .centerCrop()
                .placeholder(R.drawable.ic_no_rpoduct_png)
                .into(holder.productImage)
        } else {
            holder.productImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_no_rpoduct_png))
        }

        holder.addPrice.setOnClickListener {
            callback.onAddToFavourite(item.product)
        }

        holder.productImage.setOnClickListener {
            productCallBack.onProductClicked(item.product)
        }

        holder.name.setOnClickListener {
            productCallBack.onProductClicked(item.product)
        }
    }

    override fun isForViewType(item: Any?): Boolean = item is HistoryData

    override fun onCreateViewHolder(layoutInflater: LayoutInflater, parent: ViewGroup?): ViewHolder {
        return ViewHolder(layoutInflater.inflate(
            R.layout.item_scan_history,
            parent,
            false))
    }

    class ViewHolder(rootView: View): RecyclerView.ViewHolder(rootView) {
        val parentLayout: CardView = rootView.parentView
        val productImage: ImageView = rootView.image_product
        val name: TextView = rootView.textView_name
        val address: TextView = rootView.textView_location
        val rating: RatingBar = rootView.product_rating
        val ratingCount: TextView = rootView.textView_rating
        val price: TextView = rootView.textView_price
        val addPrice: ImageView = rootView.imageView_add
    }

    interface ItemClick {
        fun onAddToFavourite(selectedProduct: Product)
    }

    interface ProductClick {
        fun onProductClicked(selectedProduct: Product)
    }
}