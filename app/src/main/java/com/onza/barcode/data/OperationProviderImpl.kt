package com.onza.barcode.data

import com.onza.barcode.app.ApiService
import com.onza.barcode.data.model.*
import com.onza.barcode.data.model.ongoing.AddPriceModel
import com.onza.barcode.data.model.ongoing.AddProduct
import com.onza.barcode.data.model.ongoing.Count
import com.onza.barcode.data.model.ongoing.Review
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import okhttp3.RequestBody

/**
 * Created by Ilia Polozov on 04/January/2020
 */

class OperationProviderImpl(val responseHandler: ResponseHandler,
                            val apiService: ApiService): OperationProvider {

    override fun addProduct(data: AddProduct): Observable<List<Product>> {
        return apiService.addProduct(data)
            .flatMap { response -> responseHandler.handleResponse(response) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
    }

//    override fun getScanHistory(): Observable<List<HistoryData>> {
//        return apiService.getScanHistory()
//            .flatMap { response -> responseHandler.handleResponse(response) }
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribeOn(Schedulers.io())
//    }

    override fun getFavourites(): Observable<List<FavouritesResponse>> {
        return apiService.getFavourites()
            .flatMap { response -> responseHandler.handleResponse(response) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
    }

    override fun addToFavouritesProduct(id: Int, count: Int): Observable<List<FavouritesResponse>> {
        return apiService.getFavourites()
            .flatMap { response -> responseHandler.handleResponse(response) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
    }

    override fun updateFavouriteCount(id: Int, count: Int): Observable<List<Product>> {
        return apiService.updateFavouriteCount(id, Count(count))
            .flatMap { response -> responseHandler.handleResponse(response) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
    }

    override fun postProductReview(id: Int, review: Review): Observable<ProductUpdatesResponse> {
        return apiService.postProductReview(id, review)
            .flatMap { response -> responseHandler.handleResponse(response) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
    }

    override fun getCategories(): Observable<List<Category>> {
        return apiService.getCategories()
            .flatMap { response -> responseHandler.handleResponse(response) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
    }

    override fun getNearShops(lat: Double, lon: Double): Observable<List<Shop>> {
        return apiService.getNearShops(lat, lon, "", 1)
            .flatMap { response -> responseHandler.handleResponse(response) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
    }

    override fun postProduct(
        params: Map<String, RequestBody>, images: List<MultipartBody.Part>
    ): Observable<Product> {
        return apiService.postProduct(params, images)
            .flatMap { response -> responseHandler.handleResponse(response) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
    }

    override fun removeFromFavourite(id: Int): Observable<List<Product>> {
        return apiService.removeFromFavourite(id)
            .flatMap { response -> responseHandler.handleResponse(response) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
    }

    override fun removeReview(id: Int): Observable<List<Reviews>> {
        return apiService.removeReview(id)
            .flatMap { response -> responseHandler.handleResponse(response) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
    }

    override fun getCompareCategories(): Observable<CompareCategoriesResponse> {
        return apiService.getCompareCategories()
            .flatMap { response -> responseHandler.handleResponse(response) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
    }

    override fun getCompareProducts(id: Int): Observable<CompareProducts> {
        return apiService.getCompareProducts(id)
            .flatMap { response -> responseHandler.handleResponse(response) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
    }

    override fun deleteComapreCategory(id: Int): Observable<CompareUpdatesResponse> {
        return apiService.deleteComapreCategory(id)
            .flatMap { response -> responseHandler.handleResponse(response) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
    }

    override fun deleteReview(id: Int): Observable<ProductUpdatesResponse> {
        return apiService.deleteReview(id)
            .flatMap { response -> responseHandler.handleResponse(response) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
    }

    override fun getProductPrices(id: Int): Observable<List<Price>> {
        return apiService.getProductPrices(id, 0.0, 0.0)
            .flatMap { response -> responseHandler.handleResponse(response) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
    }


    override fun postProductPrice(id: Int, data: AddPriceModel): Observable<ProductUpdatesResponse> {
        return apiService.postProductPrice(id, data)
            .flatMap { response -> responseHandler.handleResponse(response) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
    }

//    override fun getProductReviews(id: Int): Observable<ReviewsResponse> {
//        return apiService.getProductReviews(id)
//            .flatMap { response -> responseHandler.handleResponse(response) }
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribeOn(Schedulers.io())
//    }

    override fun getProductDetails(id: Int): Observable<List<ProductDetail>> {
        return apiService.getProductDetails(id)
            .flatMap { response -> responseHandler.handleResponse(response) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
    }

    override fun getScannedResult(gtin: String, lat: Double, lon: Double): Observable<ProductResponse> {
        return apiService.getProductByBarCode(gtin, lat, lon)
            .flatMap { response -> responseHandler.handleResponse(response) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
    }

}