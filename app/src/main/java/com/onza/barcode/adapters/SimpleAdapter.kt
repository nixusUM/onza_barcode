package com.onza.barcode.adapters

import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AdapterDelegatesManager
import com.onza.barcode.data.model.Category
import com.onza.barcode.data.model.Shop
import java.io.File

/**
 * Created by Ilia Polozov on 12/January/2020
 */

open class SimpleAdapter(
    val items: List<*>,
    val delegatesManager: AdapterDelegatesManager<List<*>>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var filterdListShop = ArrayList<Shop>()
    private var filterdListCategories = ArrayList<Category>()

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        delegatesManager.onBindViewHolder(items, position, holder)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>) {
        delegatesManager.onBindViewHolder(items, position, holder, payloads)
    }

    override fun getItemViewType(position: Int): Int = delegatesManager.getItemViewType(items, position)

    override fun getItemCount(): Int = items.size

    fun addItem(scannedProduct: Any, position: Int) {
        var scannedProducts = items as MutableList<Any>
        this.items.add(scannedProduct)
        notifyDataSetChanged()
    }

    fun removeItem(postion: Int) {
        var scannedProducts = items as MutableList<Any>
        try {
            this.items.removeAt(postion)
        } catch (ex: IndexOutOfBoundsException) {
            Log.i("exIndex: ", ex.message)
        }
        if (this.items.isNotEmpty()) {
            notifyDataSetChanged()
        }
    }

    fun updateProduct(scannedProduct: Any) {
        var scannedProducts = items as MutableList<Any>
        this.items.add(0, scannedProduct)
        if (this.items.isNotEmpty()) {
            notifyDataSetChanged()
        }
    }

    fun filterShops(text: String) {
        var shops = items as ArrayList<Shop>
        if (filterdListShop.isEmpty()) {
            filterdListShop.addAll(shops)
        }
        shops.clear()
        if (text.isEmpty()) {
            shops.addAll(filterdListShop)
        } else {
            for (item in filterdListShop) {
                if (item.name.toLowerCase().contains(text.toLowerCase()) ||
                    item.branch.address!!.toLowerCase().contains(text.toLowerCase())) {
                    shops.add(item)
                }
            }
        }
        notifyDataSetChanged()
    }

    fun clearAdapter() {
        var shops = items as ArrayList<Shop>
        shops.clear()
        notifyDataSetChanged()
    }

    fun filterCategories(text: String) {
        var categories = items as ArrayList<Category>
        if (filterdListCategories.isEmpty()) {
            filterdListCategories.addAll(categories)
        }
        categories.clear()
        if (text.isEmpty()) {
            categories.addAll(filterdListCategories)
        } else {
            for (item in filterdListCategories) {
                if (item.name.toLowerCase().contains(text.toLowerCase())) {
                    categories.add(item)
                }
            }
        }
        notifyDataSetChanged()
    }

    fun updateShopSelected(shopsSelected: ArrayList<Shop>) {
        var shops = items as ArrayList<Shop>
        shops = shopsSelected
        notifyDataSetChanged()
    }

    fun removeImage(postition: Int) {
        var images = items as MutableList<File>
        this.items.removeAt(postition)
        if (this.items.isNotEmpty()) {
            notifyDataSetChanged()
        }
    }

    fun getItemsList(): MutableList<Any> {
        return items as MutableList<Any>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        delegatesManager.onCreateViewHolder(parent, viewType)

}