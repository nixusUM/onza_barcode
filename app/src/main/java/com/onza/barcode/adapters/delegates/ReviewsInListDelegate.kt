package com.onza.barcode.adapters.delegates

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.onza.barcode.R
import com.onza.barcode.data.model.Reviews
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.item_reviews.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Ilia Polozov on 09/February/2020
 */

class ReviewsInListDelegate(context: Context, val callback: ItemClick)
    : BaseDelegate<ReviewsInListDelegate.ViewHolder, Reviews>(context) {

    override fun onBindViewHolder(position: Int, item: Reviews, holder: ViewHolder, payloads: MutableList<Any>) {
        if (item.author.avatar_url != null) {
            Glide.with(context)
                .load(item.author.avatar_url)
                .centerCrop()
                .placeholder(R.drawable.ic_neutral_gender)
                .into(holder.avatar)
        }
        holder.name.text = item.author.name
        holder.starCount.text = item.rating.toString()
        holder.date.text = SimpleDateFormat("dd MMMM, HH:mm a", Locale.US).format(item.updated_at)
        if (item.positive_text.isNullOrEmpty()) {
            holder.viewPositive.visibility = View.GONE
        } else {
            holder.viewPositive.visibility = View.VISIBLE
            holder.textPositive.text = item.positive_text

        }
        if (item.negative_text.isNullOrEmpty()) {
            holder.viewNegative.visibility = View.GONE
        } else {
            holder.viewNegative.visibility = View.VISIBLE
            holder.textNegative.text = item.negative_text
        }

        holder.text.text = item.text
    }

    override fun isForViewType(item: Any?): Boolean = item is Reviews

    override fun onCreateViewHolder(layoutInflater: LayoutInflater, parent: ViewGroup?): ViewHolder {
        return ViewHolder(layoutInflater.inflate(
            R.layout.item_reviews,
            parent,
            false))
    }

    class ViewHolder(rootView: View): RecyclerView.ViewHolder(rootView) {
        val avatar: CircleImageView = rootView.avatar
        val name: TextView = rootView.name
        val starCount: TextView = rootView.star_count
        val date: TextView = rootView.date
        val textPositive: TextView = rootView.text_positive
        val textNegative: TextView = rootView.text_negative
        val viewPositive: LinearLayout = rootView.view_postive
        val viewNegative: LinearLayout = rootView.view_negative
        val text: TextView = rootView.text

    }

    interface ItemClick {
        fun onItemClicked()
    }
}