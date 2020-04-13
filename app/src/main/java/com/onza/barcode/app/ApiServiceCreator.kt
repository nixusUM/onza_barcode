package com.onza.barcode.app

import com.google.gson.Gson
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import android.content.Context

object ApiServiceCreator {

    fun createService(gson: Gson, context: Context): ApiService {
        val logInterceptor = HttpLoggingInterceptor()
                .apply { level = HttpLoggingInterceptor.Level.BODY }

        val client = OkHttpClient.Builder()
                .apply {
                    connectionPool(ConnectionPool(5, 10000L, TimeUnit.MILLISECONDS))
                    retryOnConnectionFailure(true)
                    addInterceptor(logInterceptor)
//                    sslSocketFactory(sslContext.socketFactory, x509TrustManager)
                    readTimeout(15L, TimeUnit.SECONDS)
                    writeTimeout(15L, TimeUnit.SECONDS)
                    connectTimeout(15L, TimeUnit.SECONDS)
                }.build()
        return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(Constants.BASE_URL)
                .client(client)
                .build()
                .create(ApiService::class.java)
    }

}