package com.onza.barcode.app

import com.onza.barcode.base.BaseResponse
import com.onza.barcode.data.Price
import com.onza.barcode.data.model.*
import com.onza.barcode.data.model.ongoing.AddPriceModel
import com.onza.barcode.data.model.ongoing.AddProduct
import com.onza.barcode.data.model.ongoing.Count
import com.onza.barcode.data.model.ongoing.Review
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

/**
 * Created by Ilia Polozov on 04/January/2020
 */

interface ApiService {

    @GET("products")
    @Headers(AUTHORIZATION)
    fun getProductByBarCode(@Query("gtin") gtin: String, @Query("lat") lat: Double, @Query("lon") lon: Double)
            : Observable<Response<BaseResponse<ProductResponse>>>

    @GET("products/{id}/prices")
    @Headers(AUTHORIZATION)
    fun getProductPrices(@Path("id") id: Int, @Query("lat") lat: Double, @Query("lon") lon: Double)
            : Observable<Response<BaseResponse<List<Price>>>>

    @POST("products")
    @Headers(AUTHORIZATION)
    fun addProduct(@Body data: AddProduct)
            : Observable<Response<BaseResponse<List<Product>>>>

    @GET("products/{id}/reviews")
    @Headers(AUTHORIZATION)
    fun getProductReviews(@Path(value = "id") id: Int, @Query("page") page: Int)
            : Observable<Response<ReviewsResponse>>

    @GET("persons/history")
    @Headers(AUTHORIZATION)
    fun getScanHistory(@Query("page") page: Int): Observable<Response<HistoryResponse>>

    @GET("persons/favorites")
    @Headers(AUTHORIZATION)
    fun getFavourites(): Observable<Response<BaseResponse<List<FavouritesResponse>>>>

    @GET("categories")
    @Headers(AUTHORIZATION)
    fun getCategories(): Observable<Response<BaseResponse<List<Category>>>>

    @GET("persons/compare/categories")
    @Headers(AUTHORIZATION)
    fun getCompareCategories(): Observable<Response<BaseResponse<CompareCategoriesResponse>>>

    @GET("persons/compare/categories/{id}/products")
    @Headers(AUTHORIZATION)
    fun getCompareProducts(@Path(value = "id") id: Int): Observable<Response<BaseResponse<CompareProducts>>>

    @GET("products/{id}/details")
    @Headers(AUTHORIZATION)
    fun getProductDetails(@Path(value = "id") id: Int): Observable<Response<BaseResponse<List<ProductDetail>>>>

    @GET("shops/nearest")
    @Headers(AUTHORIZATION)
    fun getNearShops(@Query("lat") lat: Double, @Query("lon") lon: Double): Observable<Response<BaseResponse<List<Shop>>>>

    @POST("products/{id}/prices")
    @Headers(AUTHORIZATION)
    fun postProductPrice(@Path(value = "id") id: Int, @Body data: AddPriceModel)
            : Observable<Response<BaseResponse<ProductUpdatesResponse>>>

    @POST("products/{id}/reviews")
    @Headers(AUTHORIZATION)
    fun postProductReview(@Path(value = "id") id: Int, @Body data: Review)
            : Observable<Response<BaseResponse<ProductUpdatesResponse>>>

    @DELETE("reviews/{id}")
    @Headers(AUTHORIZATION)
    fun deleteReview(@Path(value = "id") id: Int)
            : Observable<Response<BaseResponse<ProductUpdatesResponse>>>

    @POST("persons/favorites/product/{product_id}")
    @Headers(AUTHORIZATION)
    fun addToFavouritesProduct(@Path(value = "product_id") id: Int, @Body data: Count)
            : Observable<Response<BaseResponse<List<FavouritesResponse>>>>

    @PATCH("persons/favorites/product/{product_id}")
    @Headers(AUTHORIZATION)
    fun updateFavouriteCount(@Path(value = "product_id") id: Int, @Body data: Count)
            : Observable<Response<BaseResponse<List<Product>>>>

    @DELETE("persons/favorites/product/{product_id}")
    @Headers(AUTHORIZATION)
    fun removeFromFavourite(@Path(value = "product_id") id: Int)
            : Observable<Response<BaseResponse<List<Product>>>>

    @DELETE("reviews/{id}")
    @Headers(AUTHORIZATION)
    fun removeReview(@Path(value = "id") id: Int)
            : Observable<Response<BaseResponse<List<Reviews>>>>

    @DELETE("persons/compare/categories/{category_id}")
    @Headers(AUTHORIZATION)
    fun deleteComapreCategory(@Path(value = "category_id") id: Int)
            : Observable<Response<BaseResponse<CompareUpdatesResponse>>>

    @DELETE("persons/compare/products/{product_id}")
    @Headers(AUTHORIZATION)
    fun deleteComapreProduct(@Path(value = "product_id") id: Int)
            : Observable<Response<Void>>

    @POST("persons/compare/products/{product_id}")
    @Headers(AUTHORIZATION)
    fun addToCompareProduct(@Path(value = "product_id") id: Int)
            : Observable<Response<Void>>

    @Multipart
    @POST("products")
    @Headers(AUTHORIZATION)
    fun postProduct(@PartMap params: Map<String, @JvmSuppressWildcards RequestBody> , @Part images: List<MultipartBody.Part>)
            : Observable<Response<BaseResponse<Product>>>

    companion object {
        const val JSON_DATA_HEADER = "Content-Type: application/json"
        const val MULTIPART_DATA_HEADER = "Content-Type: multipart/form-data"
        const val AUTHORIZATION = "Authorization: Basic MTIzNDoxMjM0"
    }

}