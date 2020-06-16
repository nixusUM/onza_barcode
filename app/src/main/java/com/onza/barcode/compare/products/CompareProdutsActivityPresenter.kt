package com.onza.barcode.compare.products

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.onza.barcode.app.ApiService
import com.onza.barcode.app.ApiServiceCreator
import com.onza.barcode.data.model.CompareProducts
import com.onza.barcode.data.model.Product
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by Ilia Polozov on 02/March/2020
 */

class CompareProdutsActivityPresenter(val view: CompareProdutsView,
                                      val context: Context, val token: String?) {

    private var gson: Gson
    private var apiService: ApiService

    init {
        gson = GsonBuilder()
            .setLenient()
            .create()
        apiService = ApiServiceCreator.createService(token, gson, context)
    }

    fun getComparesProducts(categoryId: Int, productId: Int) {
        apiService.getCompareProductsByProduct(categoryId, productId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    if (it.body()!!.data!!.details.isNotEmpty()) {
                        view.showCompareProducts(prepareItems(it.body()!!.data!!))
                    } else {
                        view.showPlaceHolderView()
                    }
                },
                {
                    Log.i("error:", it.toString())
                    view.showMessage(it.toString())
                })
    }

    fun removeProductFromCompare(productId: Int) {
        apiService.deleteComapreProduct(productId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
//                    if (it.body()!!.data!!.details.isNotEmpty()) {
//                        view.showCompareProducts(prepareItems(it.body()!!.data!!))
//                    } else {
//                        view.showPlaceHolderView()
//                    }
                },
                {
                    Log.i("error:", it.toString())
                    view.showMessage(it.toString())
                })
    }

    private fun prepareItems(comapreList: CompareProducts): List<Product> {
        var productList = comapreList.products
        for (i in productList.indices) {
            comapreList.products[i].detail = comapreList.details
        }

        return productList
    }
}