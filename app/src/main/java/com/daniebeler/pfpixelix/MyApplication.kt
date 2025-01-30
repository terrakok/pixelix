package com.daniebeler.pfpixelix

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkerFactory
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import com.daniebeler.pfpixelix.di.Module
import com.daniebeler.pfpixelix.di.create

class MyApplication : Application(), Configuration.Provider, ImageLoaderFactory {
    lateinit var workerFactory: WorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().setWorkerFactory(workerFactory).build()

    override fun onCreate() {
        appComponent = Module::class.create(this)
        workerFactory = WorkerFactory.getDefaultWorkerFactory() //todo

        super.onCreate()
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .memoryCache(
                MemoryCache.Builder(this)
                    .maxSizePercent(0.2)
                    .build()
            )
            .diskCachePolicy(CachePolicy.ENABLED)
            .diskCache(DiskCache.Builder()
                .maxSizeBytes(50L * 1024L * 1024L)
                .directory(cacheDir)
                .build())
            .build()
    }

    companion object {
        lateinit var appComponent: Module
            private set
    }
}