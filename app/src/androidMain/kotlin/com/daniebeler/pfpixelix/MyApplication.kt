package com.daniebeler.pfpixelix

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkerFactory
import coil3.SingletonImageLoader
import com.daniebeler.pfpixelix.di.AndroidAppComponent
import com.daniebeler.pfpixelix.di.AppComponent
import com.daniebeler.pfpixelix.di.create

class MyApplication : Application(), Configuration.Provider {
    lateinit var workerFactory: WorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().setWorkerFactory(workerFactory).build()

    override fun onCreate() {
        val mainComponent = AppComponent::class.create(this, AndroidContextNavigation(this))
        SingletonImageLoader.setSafe {
            mainComponent.provideImageLoader()
        }

        appComponent = AndroidAppComponent::class.create(this, mainComponent)
        workerFactory = WorkerFactory.getDefaultWorkerFactory() //todo

        super.onCreate()
    }

    companion object {
        lateinit var appComponent: AndroidAppComponent
            private set
    }
}