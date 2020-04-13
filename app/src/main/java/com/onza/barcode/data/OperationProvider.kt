package com.onza.barcode.data

import com.onza.barcode.data.model.*
import com.onza.barcode.data.model.ongoing.AddPriceModel
import com.onza.barcode.data.model.ongoing.AddProduct
import com.onza.barcode.data.model.ongoing.Review
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody

/**
 * Created by Ilia Polozov on 04/January/2020
 */

interface OperationProvider {

    fun getScannedResult(gtin: String, lat: Double, lon: Double): Observable<ProductResponse>
//    fun getProductReviews(id: Int): Observable<ReviewsResponse>
    fun getProductDetails(id: Int): Observable<List<ProductDetail>>
    fun postProductPrice(id: Int, data: AddPriceModel): Observable<ProductUpdatesResponse>
    fun getProductPrices(id: Int): Observable<List<Price>>
    fun addProduct(data: AddProduct): Observable<List<Product>>
//    fun getScanHistory(): Observable<List<HistoryData>>
    fun getFavourites(): Observable<List<FavouritesResponse>>
    fun addToFavouritesProduct(id: Int, count: Int): Observable<List<FavouritesResponse>>
    fun updateFavouriteCount(id: Int, count: Int): Observable<List<Product>>
    fun postProductReview(id: Int, review: Review): Observable<ProductUpdatesResponse>
    fun deleteReview(id: Int): Observable<ProductUpdatesResponse>
    fun getCategories(): Observable<List<Category>>
    fun getNearShops(lat: Double, lon: Double): Observable<List<Shop>>
    fun postProduct(params: Map<String, RequestBody>, images: List<MultipartBody.Part>): Observable<Product>
    fun removeFromFavourite(id: Int): Observable<List<Product>>
    fun removeReview(id: Int): Observable<List<Reviews>>
    fun getCompareCategories(): Observable<CompareCategoriesResponse>
    fun getCompareProducts(id: Int): Observable<CompareProducts>
    fun deleteComapreCategory(id: Int): Observable<CompareUpdatesResponse>
}