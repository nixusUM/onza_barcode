package com.onza.barcode.adapters.delegates

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.onza.barcode.R
import com.onza.barcode.data.model.Product
import com.ymb.ratingbar_lib.RatingBar
import kotlinx.android.synthetic.main.fragment_product_detail.*
import kotlinx.android.synthetic.main.item_product.view.*

/**
 * Created by Ilia Polozov on 12/January/2020
 */

class ProductDelegate(context: Context, val addPriceCallback: AddPrice, val shareCallback: ShareProduct,
                      val favouriteCallback: onAddToFavourite, val productCallback: ProductCallback, val reviewCallBack: ReviewCallback)
    : BaseDelegate<ProductDelegate.ViewHolder, Product>(context) {

    private var firstTime = true

    override fun onBindViewHolder(position: Int, item: Product, holder: ViewHolder, payloads: MutableList<Any>) {
        firstTime = true
        holder.name.text = item.name
        holder.location.text = item.production_place
        var priceText = "Цен"
//        holder.location.text = item.description

        if (item.permissions!!.has_added_price) {
            if (item.avg_price != null && item.avg_price != 0f) {
                holder.addPriceView.visibility = View.GONE
                holder.priceView.visibility = View.VISIBLE
                holder.price.text = "~ " + String.format("%.2f", item.avg_price) + " Р"
                priceText = when (item.amounts!!.prices) {
                    1 -> " Цена"
                    2 -> " Цены"
                    3 -> " Цены"
                    4 -> " Цены"
                    else -> " Цен"
                }
                holder.priceCount.text = item.amounts!!.prices.toString() + priceText
            }
        } else {
            holder.priceView.visibility = View.GONE
            holder.addPriceView.visibility = View.VISIBLE
        }

        if (item.owner_rating != null) {
            holder.rating.rating = item.owner_rating!!.toFloat()
            holder.ratingStar.text = item.owner_rating.toString()
        }

        holder.rates.text = item.amounts!!.rates.toString()

        holder.addPrice.setOnClickListener { addPriceCallback.onAddPriceClicked(item, holder.adapterPosition) }
        holder.parentView.setOnClickListener { productCallback.onProductClicked(item) }
        holder.addToFavourite.setOnClickListener {
            favouriteCallback.onAddToFavouriteClicked(item)
        }

        holder.rating.setOnRatingChangedListener(object : android.widget.RatingBar.OnRatingBarChangeListener,
            RatingBar.OnRatingChangedListener {
            override fun onRatingChanged(ratingBar: android.widget.RatingBar?, rating: Float, fromUser: Boolean) {

            }

            override fun onRatingChange(p0: Float, p1: Float) {
                if (!firstTime) {
                    reviewCallBack.onAddReviewClicked(item, position, p1.toDouble())
                }
                if (firstTime) {
                    firstTime = false
                }
            }
        })

        holder.rateView.setOnClickListener {
            shareCallback.onShareProductClicked(item.reviews.isNullOrEmpty(), holder.adapterPosition, item)
        }

//        holder.addReview.setOnClickListener {
//            reviewCallBack.onAddReviewClicked(item, holder.adapterPosition)
//        }
    }

    override fun isForViewType(item: Any?): Boolean = item is Product

    override fun onCreateViewHolder(layoutInflater: LayoutInflater, parent: ViewGroup?): ViewHolder {
        return ViewHolder(layoutInflater.inflate(
            R.layout.item_product,
            parent,
            false))
    }

    class ViewHolder(rootView: View): RecyclerView.ViewHolder(rootView) {
        val name: TextView = rootView.textView_name
        val location: TextView = rootView.textView_location
        val addPrice: CardView = rootView.cardView_add_price
        val addReview: CardView = rootView.cardView_rating
        val parentView: ConstraintLayout = rootView.parentView
        val priceView: LinearLayout = rootView.view_price
        val price: TextView = rootView.textView_price
        val priceCount: TextView = rootView.textView_price_count
        val addPriceView: CardView = rootView.cardView_add_price
        val rating: RatingBar = rootView.product_rating
        val ratingStar: TextView = rootView.textView_rating_star
        val rates: TextView = rootView.textView_rates
        val addToFavourite: FrameLayout = rootView.relative_add
        val rateView: LinearLayout = rootView.lyt_rate
    }

    interface AddPrice {
        fun onAddPriceClicked(selectedProduct: Product, position: Int)
    }

    interface ProductCallback {
        fun onProductClicked(selectedProduct: Product)
    }

    interface ShareProduct {
        fun onShareProductClicked(isEmptyReviews: Boolean, position: Int, selectedProduct: Product)
    }

    interface onAddToFavourite {
        fun onAddToFavouriteClicked(selectedProduct: Product)
    }

    interface ReviewCallback {
        fun onAddReviewClicked(selectedProduct: Product, position: Int, ratingCount: Double)
    }

}