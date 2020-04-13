package com.onza.barcode.adapters.delegates

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.onza.barcode.R
import com.onza.barcode.data.model.HistoryDate
import kotlinx.android.synthetic.main.item_date.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Ilia Polozov on 03/March/2020
 */

class DateSplitterDelegate(context: Context): BaseDelegate<DateSplitterDelegate.ViewHolder, HistoryDate>(context) {

    override fun onBindViewHolder(position: Int, item: HistoryDate, holder: ViewHolder, payloads: MutableList<Any>) {
        holder.date.text = SimpleDateFormat("dd MMMM", Locale.getDefault()).format(item.date)
        holder.day.text = SimpleDateFormat("EEEE", Locale.getDefault()).format(item.date)
    }

    override fun isForViewType(item: Any?): Boolean = item is HistoryDate

    override fun onCreateViewHolder(layoutInflater: LayoutInflater, parent: ViewGroup?): ViewHolder {
        return ViewHolder(layoutInflater.inflate(
            R.layout.item_date,
            parent,
            false))
    }

    class ViewHolder(rootView: View): RecyclerView.ViewHolder(rootView) {
        val date: TextView = rootView.date
        val day: TextView = rootView.day
    }
}