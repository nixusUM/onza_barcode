package com.onza.barcode.shop

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.onza.barcode.app.ApiService
import com.onza.barcode.app.ApiServiceCreator
import com.onza.barcode.data.model.Shop
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.net.UnknownHostException
import java.util.ArrayList

/**
 * Created by Ilia Polozov on 25/January/2020
 */

class ShopActivityPresenter (val view: ShopView, val context: Context) {

    private var gson: Gson
    private var apiService: ApiService
    private var shopList = ArrayList<Shop>()

    init {
        gson = GsonBuilder()
            .setLenient()
            .create()
        apiService = ApiServiceCreator.createService(gson, context)
    }

    fun getNeearShops(lat: Double, lon: Double, page: Int) {
        apiService.getNearShops(lat, lon, page)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (
                {
                    shopList.addAll(it.body()!!.data!!)
                    view.showShopList(shopList)
                },
                {
                    errorHandling(it)
                })
    }

    private fun errorHandling(t: Throwable) {
        if (t is UnknownHostException) {
            view.showMessage(t.toString())
        }
    }
}
