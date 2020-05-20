package com.onza.barcode.dialogs.addReview

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.onza.barcode.app.ApiService
import com.onza.barcode.app.ApiServiceCreator
import com.onza.barcode.data.model.ongoing.Review
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.net.UnknownHostException

/**
 * Created by Ilia Polozov on 24/February/2020
 */

class AddReviewPresenter (val context: Context, val view: AddReviewView) {

    private var gson: Gson
    private var apiService: ApiService

    init {
        gson = GsonBuilder()
            .setLenient()
            .create()
        apiService = ApiServiceCreator.createService(gson, context)
    }

    fun addReview(productId: Int, rating: Double, shopId: Int, postive: String, negative: String, text: String) {
        apiService.postProductReview(productId, Review(rating, shopId, text, postive, negative))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (
                {
                    Log.i("response review: ", it.toString())
                    view.successAdded()
                },
                {
                    Log.i("error:", it.toString())
                    view.showMessage(it.toString())
                })
    }

    fun getNeearShops(lat: Double, lon: Double) {
        apiService.getNearShops(lat, lon, "",1)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (
                {
                    view.initShop(it.body()!!.data!!)
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