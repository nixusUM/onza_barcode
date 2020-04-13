package com.onza.barcode.adapters.delegates

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatRatingBar
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.onza.barcode.R
import com.onza.barcode.data.model.Reviews
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.item_details_review.view.*

/**
 * Created by Ilia Polozov on 28/January/2020
 */

class ReviewsDelegate(context: Context, val callback: ItemClick)
    : BaseDelegate<ReviewsDelegate.ViewHolder, Reviews>(context) {

    override fun onBindViewHolder(position: Int, item: Reviews, holder: ViewHolder, payloads: MutableList<Any>) {
        holder.rating.rating = item.rating
        holder.name.text = item.author.name
        holder.text.text = item.text

        if (item.author.avatar_url != null) {
            Glide.with(context)
                .load(item.author.avatar_url)
                .placeholder(R.drawable.ic_neutral_gender)
                .into(holder.image)
        }
    }

    override fun isForViewType(item: Any?): Boolean = item is Reviews

    override fun onCreateViewHolder(layoutInflater: LayoutInflater, parent: ViewGroup?): ViewHolder {
        return ViewHolder(layoutInflater.inflate(
            R.layout.item_details_review,
            parent,
            false))
    }

    class ViewHolder(rootView: View): RecyclerView.ViewHolder(rootView) {
        val rating: AppCompatRatingBar = rootView.rating
        val text: TextView = rootView.text
        val name: TextView = rootView.name
        val image: CircleImageView = rootView.imageView_photo

    }

    interface ItemClick {
        fun onReviewClicked()
    }
}