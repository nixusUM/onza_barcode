package com.onza.barcode.product.list

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.onza.barcode.app.ApiService
import com.onza.barcode.app.ApiServiceCreator
import com.onza.barcode.data.model.Category
import com.onza.barcode.data.model.FavouritesResponse
import com.onza.barcode.data.model.Product
import com.onza.barcode.data.model.ongoing.Count
import com.onza.barcode.main.MainActivity
import com.onza.barcode.product.detail.ProductDetaislFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by Ilia Polozov on 29/January/2020
 */

class ProductListActivityPresenter(val view: ProductListView, val context: Context) {

    private var gson: Gson
    private var apiService: ApiService

    init {
        gson = GsonBuilder()
            .setLenient()
            .create()
        apiService = ApiServiceCreator.createService(gson, context)
    }

    fun navigateToDetail(product: Product, activity: MainActivity) {
        activity.changeFragment(ProductDetaislFragment.getInstance(product), ProductDetaislFragment.javaClass.simpleName, false)
    }

    fun getFavouriteList() {
        apiService.getFavourites()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (
                {
                    view.showFavourites(prepareItemsToDisplay(it.body()!!.data!!))
                    if (it.body()!!.data!!.isNotEmpty()) {
                        view.showFavouriteView(it.body()!!.data!!)
                    }
                },
                {
                    Log.i("error:", it.toString())
                    view.showMessage(it.toString())
                })
    }

    fun removeProduct(id: Int, position: Int) {
        apiService.removeFromFavourite(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (
                {
                    view.removedProduct(position)
                },
                {
                    Log.i("error:", it.toString())
                    view.showMessage(it.toString())
                })
    }

    fun updateProduct(id: Int, count: Int) {
        apiService.updateFavouriteCount(id, Count(count))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (
                {
                    Log.i("aa:", it.toString())
                    view.updatedProduct()
                    view.showMessage("Данные о продукте обновлены")
                },
                {
                    Log.i("error:", it.toString())
                    view.showMessage("Ошибка")
                })
    }

    private fun prepareItemsToDisplay(favouriteList: List<FavouritesResponse>): List<Any> {
        if(favouriteList.isEmpty()) return emptyList()

        val preparedItems = mutableListOf<Any>()

        for (i in favouriteList.indices) {
            preparedItems.add(Category(favouriteList[i].category.id, favouriteList[i].category.name))
            for (item in favouriteList[i].products) {
                preparedItems.add(item)
            }
        }

        return preparedItems
    }
}