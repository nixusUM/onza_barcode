package com.onza.barcode.data

import android.util.Log
import io.reactivex.Observable
import com.onza.barcode.base.ApiException
import com.onza.barcode.base.BaseResponse
import org.json.JSONObject
import retrofit2.Response

/**
 * Created by Rustem Muzafarov in 2018.
 * All rights reserved.
 */
class ResponseHandlerImpl: ResponseHandler {

    override fun <T> handleResponse(response: Response<BaseResponse<T>>)
            : Observable<T> { //todo need to refactor this mess
        if (response.code() != 200) {
            val error = response.errorBody()?.string()
            if(error != null) {
                val message = JSONObject(error).getString("error")
                val text = JSONObject(message).getString(MESSAGE)
                Log.d("apiError: " , message.toString())
                throw ApiException(text.toString(), response.code())
            }
        }
        return Observable.just(response.body()!!.data)
    }

    companion object {
        const val MESSAGE = "message"
    }
}