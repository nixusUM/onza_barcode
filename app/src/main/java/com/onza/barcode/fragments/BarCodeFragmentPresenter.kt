package com.onza.barcode.fragments

import android.app.Activity
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.onza.barcode.app.ApiService
import com.onza.barcode.app.ApiServiceCreator
import com.onza.barcode.base.BaseError
import com.onza.barcode.data.model.NoProduct
import com.onza.barcode.data.model.ongoing.Count
import com.onza.barcode.data.model.ongoing.Rating
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.text.FieldPosition

/**
 * Created by Ilia Polozov on 04/January/2020
 */

class BarCodeFragmentPresenter(private var activity: Activity, private var view: BarCodeView, val token: String?) {

    private var gson: Gson
    private var apiService: ApiService
    private var gtin = ""

    init {
        gson = GsonBuilder()
            .setLenient()
            .create()
        apiService = ApiServiceCreator.createService(token, gson, activity)
    }

    fun getProductByBarCode(gtin: String, lat: Double, lon: Double) {
        if (gtin == this.gtin) {
            return
        }
        this.gtin = gtin
        apiService.getProductByBarCode(this.gtin, lat, lon)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (
                {
                    Log.i("subscribeApi", it.toString())
                    when (it.code()) {
                        404 -> {
                            view.addScannedProduct(NoProduct("temp"), false, null)
                        }
                        200 -> {
                            view.addScannedProduct(it.body()!!.data!!.product, true, it.body()!!.data!!.compare_images)
                        }
                        400 -> {
                            if (it.errorBody() != null) {
                                val mJson = JsonParser().parse(it.errorBody()!!.string())
                                view.showAttentionDialog(mJson.asJsonObject["error"].asJsonObject["title"].asString,
                                    mJson.asJsonObject["error"].asJsonObject["description"].asString)
                            }
                        }
                    }
                },
                {
                    Log.i("addProductError:", it.toString())
                    view.showError(it.toString())
                })
    }

    fun addToFavourites(id: Int, count: Int) {
        apiService.addToFavouritesProduct(id, Count(count))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (
                {
                    Log.i("aa:", it.toString())
//                    view.showFavouriteView(it.body()!!.data!!)
                },
                {
                    Log.i("error:", it.toString())
                    view.showError(it.toString())
                })
    }

    fun addReview(productId: Int, rating: Double, position: Int, gtin: String) {
        apiService.postProductJustRating(productId, Rating(rating))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (
                {
                    Log.i("response review: ", it.toString())
                    view.showAddedReviewDialog(gtin, position, it.body()!!.data!!.product_updates.owner_rating)
                },
                {
                    Log.i("error:", it.toString())
                    view.showError(it.toString())
                })
    }

    fun onProductItemClicked() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}