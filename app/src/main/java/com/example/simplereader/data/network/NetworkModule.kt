package com.example.simplereader.data.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.dnsoverhttps.DnsOverHttps
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

object NetworkModule {
    
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val dohDns = DnsOverHttps.Builder()
        .client(OkHttpClient())
        .url("https://cloudflare-dns.com/dns-query".toHttpUrl())
        .build()

    private val client = OkHttpClient.Builder()
        .dns(dohDns)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("User-Agent", "SimpleReader/1.0")
                .build()
            chain.proceed(request)
        }
        .addInterceptor(logging)
        .build()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(MangaDexApiService.BASE_URL)
        .client(client)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    val apiService: MangaDexApiService = retrofit.create(MangaDexApiService::class.java)


}
