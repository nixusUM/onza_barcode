package com.onza.barcode.history

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.onza.barcode.app.ApiService
import com.onza.barcode.app.ApiServiceCreator
import com.onza.barcode.data.model.HistoryData
import com.onza.barcode.data.model.HistoryDate
import com.onza.barcode.data.model.ongoing.Count
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Ilia Polozov on 19/January/2020
 */

class HistoryActivityPresenter(val view: HistoryView, val context: Context) {

    private var gson: Gson
    private var apiService: ApiService
    private var historyList = ArrayList<Any>()

    init {
        gson = GsonBuilder()
            .setLenient()
            .create()
        apiService = ApiServiceCreator.createService(gson, context)
    }

    fun getScanHistory(page: Int) {
        apiService.getScanHistory(page)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (
                {
                    historyList.addAll(prepareItems(it.body()!!.data))
                    view.showProductHistory(historyList)
                },
                {
                    Log.i("error:", it.toString())
                    view.showMessage(it.toString())
                })
    }

    fun addToFavourites(id: Int, count: Int) {
         apiService.addToFavouritesProduct(id, Count(count))
             .subscribeOn(Schedulers.io())
             .observeOn(AndroidSchedulers.mainThread())
             .subscribe (
                 {
                     Log.i("aa:", it.toString())
                     view.showFavouriteView(it.body()!!.data!!)
                 },
                 {
                     Log.i("error:", it.toString())
                     view.showMessage(it.toString())
                 })
    }

    private fun prepareItems(list: List<HistoryData>): List<Any> {
        val cal = Calendar.getInstance(Locale.US)
        cal.time = Date(list[0].updated_at * 1000)

        val preparedItems = mutableListOf<Any>()
        var currentTime = SimpleDateFormat("dd MMMM", Locale.getDefault()).format(list[0].updated_at * 1000)
        preparedItems.add(HistoryDate(list[0].updated_at * 1000))

        for (i in list.indices) {
            if (currentTime != SimpleDateFormat("dd MMMM", Locale.getDefault()).format(list[i].updated_at * 1000)) {
                currentTime = SimpleDateFormat("dd MMMM", Locale.getDefault()).format(list[i].updated_at * 1000)
                preparedItems.add(HistoryDate(list[i].updated_at * 1000))
            }
            preparedItems.add(list[i])
        }

        return preparedItems
    }

}