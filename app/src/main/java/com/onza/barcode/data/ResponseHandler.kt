package com.onza.barcode.data

import com.onza.barcode.base.BaseResponse
import io.reactivex.Observable
import retrofit2.Response

/**
 * Created by Ilia Polozov on 04/January/2020
 */

interface ResponseHandler {
    fun <T> handleResponse(response: Response<BaseResponse<T>>): Observable<T>
}