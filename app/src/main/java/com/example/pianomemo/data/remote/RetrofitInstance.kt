package com.example.pianomemo.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "https://api.spotify.com/"

    // OkHttpClient に Interceptor を追加して Authorization ヘッダーを自動付与
    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer {your_access_token}")
                .build()
            chain.proceed(request)
        }
        .build()

    // Retrofit のインスタンスを lazy 初期化で生成
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // SpotifyApiService の実装を取得
    val api: SpotifyApiService by lazy {
        retrofit.create(SpotifyApiService::class.java)
    }
}