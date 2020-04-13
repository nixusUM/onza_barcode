package com.onza.barcode.adapters.delegates

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate

/**
 * Created by Ilia Polozov on 12/January/2020
 */

abstract class BaseDelegate<ViewHolder: RecyclerView.ViewHolder, in ItemType>(val context: Context)
    : AdapterDelegate<List<*>>() {

    final override fun onBindViewHolder(
        items: List<*>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ) {
        val viewHolder = holder as? ViewHolder ?: return
        val item = items[position] as? ItemType ?: return
        onBindViewHolder(position, item, viewHolder, payloads)
    }

    final override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return onCreateViewHolder(LayoutInflater.from(context), parent)
    }

    abstract fun onBindViewHolder(
        position: Int,
        item: ItemType,
        holder: ViewHolder,
        payloads: MutableList<Any>
    )

    final override fun isForViewType(items: List<*>, position: Int): Boolean =
        isForViewType(items[position])

    abstract fun isForViewType(item: Any?): Boolean

    abstract fun onCreateViewHolder(layoutInflater: LayoutInflater, parent: ViewGroup?): ViewHolder
}

