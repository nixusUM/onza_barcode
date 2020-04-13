package com.onza.barcode.adapters.delegates

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.onza.barcode.R
import com.onza.barcode.data.model.FavouriteProducts
import com.onza.barcode.data.model.Product
import com.ymb.ratingbar_lib.RatingBar
import kotlinx.android.synthetic.main.item_product_in_list.view.*

/**
 * Created by Ilia Polozov on 29/January/2020
 */

class ProductInListDelegate(context: Context, val callback: ItemClick)
    : BaseDelegate<ProductInListDelegate.ViewHolder, FavouriteProducts>(context) {

    override fun onBindViewHolder(position: Int, item: FavouriteProducts, holder: ViewHolder, payloads: MutableList<Any>) {
        holder.name.text = item.product.name
        holder.description.text = item.product.description
        holder.price.text = "~ " + String.format("%.2f", item.product.avg_price) + " Р"
        holder.rating.rating = item.product.rating.toFloat()
        holder.ratingText.text = item.product.rating.toString()
        holder.peopleCount.text = item.product.amounts!!.rates.toString()
        holder.commentCount.text = item.product.amounts.reviews.toString()
        holder.productCount.text = item.count.toString() + " Шт"

        if (!item.product.images.isNullOrEmpty()) {
            Glide.with(context)
                .load(item.product.images[0].url)
                .centerCrop()
                .placeholder(R.drawable.ic_no_rpoduct_png)
                .into(holder.productImage)
        } else {
            holder.productImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_no_rpoduct_png))
        }

        holder.parentView.setOnClickListener {
            callback.onProductClicked(item.product)
        }
    }

    override fun isForViewType(item: Any?): Boolean = item is FavouriteProducts

    override fun onCreateViewHolder(layoutInflater: LayoutInflater, parent: ViewGroup?): ViewHolder {
        return ViewHolder(layoutInflater.inflate(
            R.layout.item_product_in_list,
            parent,
            false))
    }

    class ViewHolder(rootView: View): RecyclerView.ViewHolder(rootView) {
        val name: TextView = rootView.title
        val productImage: ImageView = rootView.image_product
        val description: TextView = rootView.description
        val price: TextView = rootView.price
        val rating: RatingBar = rootView.product_rating
        val ratingText: TextView = rootView.txt_rating
        val peopleCount: TextView = rootView.people_count
        val commentCount: TextView = rootView.comment_count
        val productCount: TextView = rootView.product_count
        val parentView: ConstraintLayout = rootView.parentView
    }

    interface ItemClick {
        fun onProductClicked(product: Product)
    }
}