package com.putrash.ocbcmobile

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.support.multidex.MultiDex
import androidx.biometric.BiometricManager
import com.putrash.common.Constants
import com.putrash.common.Constants.AUTH_PREF
import com.putrash.common.preferences.PreferencesHelper
import com.putrash.common.preferences.PreferencesHelper.set
import com.putrash.common.preferences.PreferencesKey
import com.putrash.ocbcmobile.di.appModule
import com.putrash.ocbcmobile.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class App : Application() {

    companion object {
        lateinit var instance: App
            private set
    }

    val preferences: SharedPreferences by lazy {
        PreferencesHelper.init(this@App, resources.getString(R.string.app_name))
    }

    val authPreferences: SharedPreferences by lazy {
        PreferencesHelper.init(this@App, AUTH_PREF)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@App)
            androidFileProperties()
            modules(appModule, viewModelModule)
        }

        val biometric = BiometricManager.from(this).canAuthenticate()
        authPreferences[PreferencesKey.HAS_BIOMETRIC] = biometric
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}