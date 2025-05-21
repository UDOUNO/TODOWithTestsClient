package com.example.todo.data

import android.util.Log
import android.widget.Toast
import com.example.todo.BuildConfig
import com.example.todo.R
import com.example.todo.TODOApplication
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

object Retrofit {
    private val BASE_URL
        get()=BuildConfig.RETROFIT_URL
    private val client = OkHttpClient()
    private val interceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    private val clientBuilder: OkHttpClient.Builder =
        client.newBuilder().addInterceptor(interceptor).addInterceptor(Interceptor { chain ->
            val original: Request = chain.request()
            val requestBuilder: Request.Builder = original.newBuilder()
            val request: Request = requestBuilder.build()
            val resp = chain.proceed(request)

            if (resp.code == 400) {
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(
                        TODOApplication.getApp().baseContext,
                        TODOApplication.getApp().baseContext.getString(R.string.error400),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            return@Interceptor resp
        })
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(Json.asConverterFactory(getContentType())).client(
                clientBuilder.build()
            ).build()
    }

    private fun getContentType() = "application/json".toMediaType()

    val Event: EventApi by lazy {
        retrofit.create(EventApi::class.java)
    }
}