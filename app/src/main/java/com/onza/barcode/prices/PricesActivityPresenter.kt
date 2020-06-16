package com.onza.barcode.prices

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.onza.barcode.app.ApiService
import com.onza.barcode.app.ApiServiceCreator
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by Ilia Polozov on 09/February/2020
 */

class PricesActivityPresenter(val view: PriceActivityView, val context: Context, val token: String?) {

    private var gson: Gson
    private var apiService: ApiService

    init {
        gson = GsonBuilder()
            .setLenient()
            .create()
        apiService = ApiServiceCreator.createService(token, gson, context)
    }

    fun getProductPrices(productId: Int, lat: Double, lon: Double) {
        apiService.getProductPrices(productId, lat, lon)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (
                {
                    view.showProductPrices(it.body()!!.data!!)
                },
                {
                    Log.i("error:", it.toString())
                    view.showMessage(it.toString())
                })
    }

}