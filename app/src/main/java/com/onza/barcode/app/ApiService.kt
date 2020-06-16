package com.onza.barcode.app

import com.onza.barcode.base.BaseResponse
import com.onza.barcode.data.Price
import com.onza.barcode.data.model.*
import com.onza.barcode.data.model.ongoing.*
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
    @Headers(JSON_DATA_HEADER)
    fun getProductByBarCode(@Query("gtin") gtin: String, @Query("lat") lat: Double, @Query("lon") lon: Double)
            : Observable<Response<BaseResponse<ProductResponse>>>

    @GET("products/{id}")
    @Headers(JSON_DATA_HEADER)
    fun getProductById(@Path("id") id: Int, @Query("lat") lat: Double, @Query("lon") lon: Double)
            : Observable<Response<BaseResponse<ProductResponse>>>

    @GET("products/{id}/prices")
    @Headers(JSON_DATA_HEADER)
    fun getProductPrices(@Path("id") id: Int, @Query("lat") lat: Double, @Query("lon") lon: Double)
            : Observable<Response<BaseResponse<List<Price>>>>

    @POST("products")
    @Headers(JSON_DATA_HEADER)
    fun addProduct(@Body data: AddProduct)
            : Observable<Response<BaseResponse<List<Product>>>>

    @GET("products/{id}/reviews")
    @Headers(JSON_DATA_HEADER)
    fun getProductReviews(@Path(value = "id") id: Int, @Query("page") page: Int)
            : Observable<Response<ReviewsResponse>>

    @GET("persons/history")
    @Headers(JSON_DATA_HEADER)
    fun getScanHistory(@Query("page") page: Int): Observable<Response<HistoryResponse>>

    @GET("persons/favorites")
    @Headers(JSON_DATA_HEADER)
    fun getFavourites(): Observable<Response<BaseResponse<List<FavouritesResponse>>>>

    @GET("categories")
    @Headers(JSON_DATA_HEADER)
    fun getCategories(): Observable<Response<BaseResponse<List<Category>>>>

    @GET("persons/compare/categories")
    @Headers(JSON_DATA_HEADER)
    fun getCompareCategories(): Observable<Response<BaseResponse<CompareCategoriesResponse>>>

    @GET("persons/compare/categories/{id}/products")
    @Headers(JSON_DATA_HEADER)
    fun getCompareProducts(@Path(value = "id") id: Int): Observable<Response<BaseResponse<CompareProducts>>>

    @GET("persons/compare/categories/{id}/products")
    @Headers(JSON_DATA_HEADER)
    fun getCompareProductsByProduct(@Path(value = "id") id: Int, @Query("target_product_id") targetId: Int): Observable<Response<BaseResponse<CompareProducts>>>

    @GET("products/{id}/details")
    @Headers(JSON_DATA_HEADER)
    fun getProductDetails(@Path(value = "id") id: Int): Observable<Response<BaseResponse<List<ProductDetail>>>>

    @GET("shops/nearest")
    @Headers(JSON_DATA_HEADER)
    fun getNearShops(@Query("lat") lat: Double, @Query("lon") lon: Double,  @Query("q") q: String, @Query("page") page: Int): Observable<Response<BaseResponse<List<Shop>>>>

    @POST("products/{id}/prices")
    @Headers(JSON_DATA_HEADER)
    fun postProductPrice(@Path(value = "id") id: Int, @Body data: AddPriceModel)
            : Observable<Response<BaseResponse<ProductUpdatesResponse>>>

    @POST("products/{id}/reviews")
    @Headers(JSON_DATA_HEADER)
    fun postProductReview(@Path(value = "id") id: Int, @Body data: Review)
            : Observable<Response<BaseResponse<ProductUpdatesResponse>>>

    @POST("products/{id}/reviews")
    @Headers(JSON_DATA_HEADER)
    fun postProductJustRating(@Path(value = "id") id: Int, @Body data: Rating)
            : Observable<Response<BaseResponse<ProductUpdatesResponse>>>

    @DELETE("reviews/{id}")
    @Headers(JSON_DATA_HEADER)
    fun deleteReview(@Path(value = "id") id: Int)
            : Observable<Response<BaseResponse<ProductUpdatesResponse>>>

    @POST("persons/favorites/product/{product_id}")
    @Headers(JSON_DATA_HEADER)
    fun addToFavouritesProduct(@Path(value = "product_id") id: Int, @Body data: Count)
            : Observable<Response<BaseResponse<List<FavouritesResponse>>>>

    @PATCH("persons/favorites/product/{product_id}")
    @Headers(JSON_DATA_HEADER)
    fun updateFavouriteCount(@Path(value = "product_id") id: Int, @Body data: Count)
            : Observable<Response<BaseResponse<List<Product>>>>

    @DELETE("persons/favorites/product/{product_id}")
    @Headers(JSON_DATA_HEADER)
    fun removeFromFavourite(@Path(value = "product_id") id: Int)
            : Observable<Response<BaseResponse<List<Product>>>>

    @DELETE("reviews/{id}")
    @Headers(JSON_DATA_HEADER)
    fun removeReview(@Path(value = "id") id: Int)
            : Observable<Response<BaseResponse<List<Reviews>>>>

    @DELETE("persons/compare/categories/{category_id}")
    @Headers(JSON_DATA_HEADER)
    fun deleteComapreCategory(@Path(value = "category_id") id: Int)
            : Observable<Response<BaseResponse<CompareUpdatesResponse>>>

    @DELETE("persons/compare/products/{product_id}")
    @Headers(JSON_DATA_HEADER)
    fun deleteComapreProduct(@Path(value = "product_id") id: Int)
            : Observable<Response<Void>>

    @POST("persons/compare/products/{product_id}")
    @Headers(JSON_DATA_HEADER)
    fun addToCompareProduct(@Path(value = "product_id") id: Int)
            : Observable<Response<Void>>

    @Multipart
    @POST("products")
    @Headers(JSON_DATA_HEADER)
    fun postProduct(@PartMap params: Map<String, @JvmSuppressWildcards RequestBody> , @Part images: List<MultipartBody.Part>)
            : Observable<Response<BaseResponse<Product>>>

    companion object {
        const val JSON_DATA_HEADER = "Content-Type: application/json"
        const val MULTIPART_DATA_HEADER = "Content-Type: multipart/form-data"
        const val AUTHORIZATION = "Authorization: Basic MTIzNDoxMjM0"
    }

}