package com.onza.barcode.adapters.delegates

import android.content.Context
import android.graphics.Point
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.onza.barcode.R
import com.onza.barcode.data.model.*
import kotlinx.android.synthetic.main.item_compare_product.view.*
import kotlinx.android.synthetic.main.item_compare_product.view.title
import kotlinx.android.synthetic.main.item_properties.view.*
import kotlinx.android.synthetic.main.item_properties.view.hint
import kotlinx.android.synthetic.main.view_product_properties.view.*

/**
 * Created by Ilia Polozov on 02/March/2020
 */

class CompareProductDelegate(context: Context, val callback: ItemClick)
    : BaseDelegate<CompareProductDelegate.ViewHolder, Product>(context) {

    override fun onBindViewHolder(position: Int, item: Product, holder: ViewHolder, payloads: MutableList<Any>) {
        holder.name.text = item.name
        holder.location.text = item.production_place
        holder.price.text = "~ " + String.format("%.2f", item.avg_price) + " Р"

        if (item.image != null) {
            Glide.with(context)
                .load(item.image.url)
                .placeholder(R.drawable.ic_no_product_grey)
                .into(holder.productImage)
        } else {
            holder.productImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_no_product_grey))
        }

        if (position != 0) {
            holder.title.visibility = View.INVISIBLE
            holder.titleReview.visibility = View.INVISIBLE
        } else {
            holder.title.visibility = View.VISIBLE
            holder.titleReview.visibility = View.VISIBLE
        }

        holder.location.text = item.production_place
        holder.rating.text = item.rating.toString()
        holder.rates.text = item.amounts!!.rates.toString()
        holder.rates.text = item.amounts.reviews.toString()

        holder.propertyView.removeAllViews()
        for(detail in item.detail!!) {
            val property = cratePropertyViews(detail, holder.adapterPosition)
            holder.propertyView.addView(property)
        }
    }

    private fun cratePropertyViews(detail: CompareProperty, position: Int): LinearLayout {
        val linflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val lyt = linflater.inflate(R.layout.view_product_properties, null) as LinearLayout
        val lytProperty = linflater.inflate(R.layout.item_compare_properties, null) as LinearLayout
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)

        lyt.layoutParams = params
        lytProperty.layoutParams = params
        lyt.title.text = detail.title
        lyt.lyt_properties.removeAllViews()

        for(property in detail.properties) {
            val proertfds = createProperites(property, property.values[position].value)
            lyt.lyt_properties.addView(proertfds)
        }

        return lyt
    }

    private fun createProperites(property: ProductCompareDetail, value: String): LinearLayout {
        val linflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val lytProperty = linflater.inflate(R.layout.item_properties, null) as LinearLayout
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT)

        lytProperty.layoutParams = params
        lytProperty.hint.text = property.title
        lytProperty.value.text = value

        return lytProperty
    }

    private fun getScreenWidth(): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val size = Point()
        display.getSize(size)
        return size.x
    }

    override fun isForViewType(item: Any?): Boolean = item is Product

    override fun onCreateViewHolder(layoutInflater: LayoutInflater, parent: ViewGroup?): ViewHolder {
        val viewHolder = layoutInflater.inflate(R.layout.item_compare_product, parent, false)
        viewHolder.layoutParams.width = getScreenWidth() / 2
        return ViewHolder(viewHolder)
    }

    class ViewHolder(rootView: View): RecyclerView.ViewHolder(rootView) {
        val name: TextView = rootView.textView_name
        val location: TextView = rootView.textView_location
        val price: TextView = rootView.textView_price
        val productImage: ImageView = rootView.image_product
        val rating: TextView = rootView.textView_rating_star
        val rates: TextView = rootView.textView_rates
        val title: TextView = rootView.title
        val titleReview: TextView = rootView.title_count_review
        val reviews: TextView = rootView.textView_reviews
        val propertyView: LinearLayout = rootView.view_product_data
    }

    interface ItemClick {
        fun removeProduct(position: Int)
    }
}