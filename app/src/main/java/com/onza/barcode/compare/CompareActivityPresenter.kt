package com.onza.barcode.compare

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.onza.barcode.app.ApiService
import com.onza.barcode.app.ApiServiceCreator
import com.onza.barcode.compare.products.CompareProdutsFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by Ilia Polozov on 01/March/2020
 */

class CompareActivityPresenter (val view: CompareView,
                                val context: Context) {

    private var gson: Gson
    private var apiService: ApiService

    init {
        gson = GsonBuilder()
            .setLenient()
            .create()
        apiService = ApiServiceCreator.createService(gson, context)
    }

    fun getCompares() {
        apiService.getCompareCategories()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    view.showCompareCategories(it.body()!!.data!!.categories)
                },
                {
                    Log.i("error:", it.toString())
                    view.showMessage(it.toString())
                })
    }

    fun navigateToScanScreen() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun navigateToCompareProductScreen(categoryId: Int, categoryName: String) {
//        val intent = CompareProdutsFragment.getStarterIntent(activity, categoryId, categoryName)
//        activity.startActivityForResult(intent, 555)
    }

    fun deleteCompareCategory(id: Int, position: Int) {
        apiService.deleteComapreCategory(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    view.removedCompareCategory(position)
                },
                {
                    Log.i("error:", it.toString())
                    view.showMessage(it.toString())
                })
    }
}