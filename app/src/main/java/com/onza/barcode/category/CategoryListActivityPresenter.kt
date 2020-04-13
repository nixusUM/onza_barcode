package com.onza.barcode.category

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.onza.barcode.app.ApiService
import com.onza.barcode.app.ApiServiceCreator
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by Ilia Polozov on 26/February/2020
 */

class CategoryListActivityPresenter(val view: CategoryListView,
                                    val context: Context) {

    private var gson: Gson
    private var apiService: ApiService

    init {
        gson = GsonBuilder()
            .setLenient()
            .create()
        apiService = ApiServiceCreator.createService(gson, context)
    }

    fun getCategories() {
        apiService.getCategories()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError {
                Log.i("error: ", it.toString())
                view.showMessage(it.toString())
            }.subscribe {
                view.showCategoryList(it.body()!!.data!!)
            }
    }

}
