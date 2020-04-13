package com.onza.barcode.dialogs.addPrice

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.onza.barcode.app.ApiService
import com.onza.barcode.app.ApiServiceCreator
import com.onza.barcode.data.model.Product
import com.onza.barcode.data.model.ongoing.AddPriceModel
import com.onza.barcode.shop.ShopActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.net.UnknownHostException

/**
 * Created by Ilia Polozov on 24/February/2020
 */

class AddPricePresenter(val view: AddPriceView, val context: Context) {

    private var gson: Gson
    private var apiService: ApiService

    init {
        gson = GsonBuilder()
            .setLenient()
            .create()
        apiService = ApiServiceCreator.createService(gson, context)
    }

    fun addPriceToProduct(id: Int, branchId: Int, price: Float) {
        apiService.postProductPrice(id, AddPriceModel(branchId, price))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (
                {
                    view.successAdded(it.body()!!.data!!.product_price)
                    Log.i("add price OK:", it.toString())
                },
                {
                    Log.i("error:", it.toString())
                    view.showMessage(it.toString())
                })
    }

    fun getNeearShops(lat: Double, lon: Double) {
        apiService.getNearShops(lat, lon)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (
                {
                    view.initShopList(it.body()!!.data!!)
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