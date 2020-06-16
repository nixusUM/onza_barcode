package com.onza.barcode.product.fragments.detail

import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.onza.barcode.app.ApiService
import com.onza.barcode.app.ApiServiceCreator
import com.onza.barcode.data.model.NoProduct
import com.onza.barcode.data.model.Product
import com.onza.barcode.data.model.Reviews
import com.onza.barcode.data.model.ongoing.Count
import com.onza.barcode.data.model.ongoing.Rating
import com.onza.barcode.data.model.ongoing.Review
import com.onza.barcode.reviews.ReviewsFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by Ilia Polozov on 28/January/2020
 */

class DetailFragmentPresenter(val view: DetailFragmentView, val activity: FragmentActivity, val token: String?) {

    private var gson: Gson
    private var apiService: ApiService

    init {
        gson = GsonBuilder()
            .setLenient()
            .create()
        apiService = ApiServiceCreator.createService(token, gson, activity)
    }

    fun addToFavourites(id: Int, count: Int) {
        apiService.addToFavouritesProduct(id, Count(count))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (
                {
                    view.showError("Продукт добавлен в список")
                },
                {
                    errorHandling(it)
                })
    }

    fun addToCompareList(id: Int) {
        apiService.addToCompareProduct(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (
                {
                    getProductById(id, 0.0, 0.0)
                },
                {
                    errorHandling(it)
                })
    }

    fun removeFromComareList(id: Int) {
        apiService.deleteComapreProduct(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (
                {
                    getProductById(id, 0.0, 0.0)
                },
                {
                    errorHandling(it)
                })
    }

    fun onViewCreated(id: Int) {
        apiService.getProductReviews(id, 1)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (
                {
                    val reviews = ArrayList<Reviews>()
                    for (i in it.body()!!.data!!.indices) {
                        if (i < 5) {
                            reviews.add(it.body()!!.data!![i])
                        }
                    }
                    view.showReviwes(reviews, it.body()!!.total)
                },
                {
                    Log.i("error:", it.toString())
                    view.showError(it.toString())
                })
    }

    private fun errorHandling(t: Throwable) {
        view.showError(t.toString())
    }

    fun getProductById(productId: Int, lat: Double, lon: Double) {
        apiService.getProductById(productId, lat, lon)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (
                {
                    Log.i("subscribeApi", it.toString())
                    when (it.code()) {
                        200 -> view.initProductData(it.body()!!.data!!.product)
                    }
                },
                {
                    Log.i("addProductError:", it.toString())
                    view.showError(it.toString())
                })
    }

    fun addReview(productId: Int, rating: Double) {
        apiService.postProductJustRating(productId, Rating(rating))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (
                {
                    Log.i("response review: ", it.toString())
                    view.showAddedReviewDialog(it.body()!!.data!!.product_updates.owner_rating)
                },
                {
                    Log.i("error:", it.toString())
                    view.showError(it.toString())
                })
    }

}