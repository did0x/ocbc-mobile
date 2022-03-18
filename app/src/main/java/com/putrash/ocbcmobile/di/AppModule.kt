package com.putrash.ocbcmobile.di

import com.putrash.common.Constants.AUTHORIZATION
import com.putrash.common.preferences.PreferencesHelper.get
import com.putrash.common.preferences.PreferencesKey
import com.putrash.data.Api
import com.putrash.data.BuildConfig.BASE_URL
import com.putrash.ocbcmobile.App
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

var appModule = module {
    factory { provideHttpLogging() }
    factory { provideAuthenticator() }
    single { provideOkHttpClient(get(), get()) }
    single { provideRetrofit(get()) }
}

fun provideHttpLogging() = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}

fun provideAuthenticator() = Authenticator { _, response ->
    val token = App.instance.preferences[PreferencesKey.AUTH_KEY, ""]
    if (token.isEmpty()) {
        return@Authenticator null
    }
    if (response.request.header(AUTHORIZATION) != token) {
        return@Authenticator response.request.newBuilder()
            .header(AUTHORIZATION, token)
            .build()
    }
    return@Authenticator null
}

fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor, authenticator: Authenticator) = OkHttpClient().newBuilder()
    .addInterceptor(loggingInterceptor)
    .authenticator(authenticator)
    .build()

fun provideRetrofit(okHttp: OkHttpClient): Api = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .client(okHttp)
    .addConverterFactory(GsonConverterFactory.create())
    .build()
    .create(Api::class.java)

