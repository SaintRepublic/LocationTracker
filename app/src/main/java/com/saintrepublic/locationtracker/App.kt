package com.saintrepublic.locationtracker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.saintrepublic.locationtracker.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App: Application() {

    companion object {
        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }
    }

    override fun onCreate() {
        startKoin {
            androidContext(this@App)
            modules(appModule)
        }
        super.onCreate()
    }

}