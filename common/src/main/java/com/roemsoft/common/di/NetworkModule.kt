package com.roemsoft.common.di

import com.roemsoft.common.api.BaseUrlInterceptor
import com.roemsoft.common.api.HttpConfig
import com.roemsoft.common.api.LogInterceptor
import com.roemsoft.common.api.SamePostInterceptor
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit

class NetworkModule private constructor() {

    companion object {
        val instance: NetworkModule by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            NetworkModule()
        }
    }

    val client: OkHttpClient by lazy { provideOkHttpClient() }

    val retrofit: Retrofit by lazy { provideRetrofit() }

    private fun provideOkHttpClient(): OkHttpClient {
        Timber.tag("OKHTTP").i("====> BUILD OKHTTP CLIENT")
        val builder = OkHttpClient().newBuilder()
        builder.run {
            //    addInterceptor(httpLoggingInterceptor)
            addInterceptor(LogInterceptor())
            addInterceptor(BaseUrlInterceptor())
            addInterceptor(SamePostInterceptor())
            connectTimeout(HttpConfig.DEFAULT_TIME_OUT, TimeUnit.SECONDS)
            readTimeout(HttpConfig.DEFAULT_TIME_OUT, TimeUnit.SECONDS)
            writeTimeout(HttpConfig.DEFAULT_TIME_OUT, TimeUnit.SECONDS)
            retryOnConnectionFailure(false)
        }
        return builder.build()
    }

    private fun provideRetrofit(): Retrofit {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        return Retrofit.Builder()
            .client(client)
            .baseUrl(HttpConfig.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
        //    .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}