package com.onza.barcode.reviews

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.onza.barcode.app.ApiService
import com.onza.barcode.app.ApiServiceCreator
import com.onza.barcode.data.model.Product
import com.onza.barcode.data.model.Reviews
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by Ilia Polozov on 09/February/2020
 */

class ReviewsActivityPresenter(val view: ReviewsView, val context: Context) {

    private var gson: Gson
    private var apiService: ApiService
    private var reviews = ArrayList<Reviews>()

    init {
        gson = GsonBuilder()
            .setLenient()
            .create()
        apiService = ApiServiceCreator.createService(gson, context)
    }

    fun getReviews(productId: Int, page: Int) {
        apiService.getProductReviews(productId, page)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (
                {
//                    if (it.body()!!.last_page != page) {
                        reviews.addAll(it.body()!!.data)
                        view.showReviews(reviews)
//                    }
                },
                {
                    Log.i("error:", it.toString())
                    view.showMessage(it.toString())
                })
    }

    fun onAddReviewClicked(selectedProduct: Product) {
//        val intent = AddReviewActivity.getStarterIntent(context, selectedProduct, 0)
//        context.startActivity(intent)
    }

    fun removeReview(id: Int, position: Int) {
        apiService.deleteReview(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (
                {
                    view.removeReivew(position)
                },
                {
                    Log.i("error:", it.toString())
                    view.showMessage(it.toString())
                })
    }

}