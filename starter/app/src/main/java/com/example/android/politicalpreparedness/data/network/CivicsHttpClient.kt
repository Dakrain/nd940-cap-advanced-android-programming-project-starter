package com.example.android.politicalpreparedness.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

class CivicsHttpClient : OkHttpClient() {

    companion object {

        const val API_KEY =
            "AIzaSyDsVisXhU5uE3KYM1z_vrkHGcqBkqEVEUg" //TODO: Place your API Key Here

        fun getClient(): OkHttpClient {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            return Builder()
                .addInterceptor { chain ->
                    val original = chain.request()
                    val url = original
                        .url
                        .newBuilder()
                        .addQueryParameter("key", API_KEY)
                        .build()
                    val request = original
                        .newBuilder()
                        .url(url)
                        .build()
                    chain.proceed(request)
                }
                .addInterceptor(loggingInterceptor)
                .build()
        }

    }

}