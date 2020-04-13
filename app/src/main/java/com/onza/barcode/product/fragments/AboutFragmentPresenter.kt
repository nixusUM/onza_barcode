package com.onza.barcode.product.fragments

import androidx.fragment.app.FragmentActivity
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.onza.barcode.app.ApiService
import com.onza.barcode.app.ApiServiceCreator
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by Ilia Polozov on 28/February/2020
 */

class AboutFragmentPresenter(val view: AboutFragmentView, val activity: FragmentActivity) {

    private var gson: Gson
    private var apiService: ApiService

    init {
        gson = GsonBuilder()
            .setLenient()
            .create()
        apiService = ApiServiceCreator.createService(gson, activity)
    }

    fun getProductDetail(id: Int) {
        apiService.getProductDetails(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (
                {
                    view.showAboutProduct(it.body()!!.data!!)
                },
                {
                    errorHandling(it)
                })
    }

    private fun errorHandling(t: Throwable) {
        view.showError(t.message)
    }
}