package com.onza.barcode.adapters.delegates

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.esafirm.imagepicker.model.Image
import com.onza.barcode.R
import kotlinx.android.synthetic.main.item_product_image.view.*

/**
 * Created by Ilia Polozov on 26/February/2020
 */

class ProductImageDelegate(context: Context, val callback: ItemClick)
    : BaseDelegate<ProductImageDelegate.ViewHolder, Image>(context) {

    override fun onBindViewHolder(position: Int, item: Image, holder: ViewHolder, payloads: MutableList<Any>) {
        Glide.with(context)
            .load(item.path)
            .centerCrop()
            .placeholder(R.drawable.ic_product_placeholder)
            .into(holder.image)

        holder.remove.setOnClickListener {
            callback.removeImage(holder.adapterPosition)
        }
    }

    override fun isForViewType(item: Any?): Boolean = item is Image

    override fun onCreateViewHolder(layoutInflater: LayoutInflater, parent: ViewGroup?): ViewHolder {
        return ViewHolder(layoutInflater.inflate(
            R.layout.item_product_image,
            parent,
            false))
    }

    class ViewHolder(rootView: View): RecyclerView.ViewHolder(rootView) {
        val parentView: CardView = rootView.parentView
        val image: ImageView = rootView.image
        val remove: ImageView = rootView.remove
    }

    interface ItemClick {
        fun removeImage(postion: Int)
    }
}