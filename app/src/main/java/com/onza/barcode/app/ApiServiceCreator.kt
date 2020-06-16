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

    fun createService(token: String?, gson: Gson, context: Context): ApiService {
        val logInterceptor = HttpLoggingInterceptor()
                .apply { level = HttpLoggingInterceptor.Level.BODY }

        val client = OkHttpClient.Builder()
                .apply {
                    connectionPool(ConnectionPool(5, 10000L, TimeUnit.MILLISECONDS))
                    retryOnConnectionFailure(true)
                    addInterceptor(logInterceptor)
                    addInterceptor(AuthInterceptor(token))
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

    class AuthInterceptor(val token: String?): Interceptor {

        override fun intercept(chain: Interceptor.Chain?): Response? {
            synchronized(lock) {
                return chain?.let {
                    it.request().let {
                        val newRequest: Request =
                                if(token != null) {
                                    it.newBuilder().addHeader("Authorization", "Basic  $token").build()
                                } else {
                                    it.newBuilder().build()
                                }
                        chain.proceed(newRequest)
                    }
                }
            }
        }

        companion object {
            val lock = Any()
        }
    }

}