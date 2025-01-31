package com.daniebeler.pfpixelix.di

import HostSelectionInterceptor
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.okio.OkioStorage
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import co.touchlab.kermit.Logger
import coil3.ImageLoader
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.request.CachePolicy
import com.daniebeler.pfpixelix.data.remote.PixelfedApi
import com.daniebeler.pfpixelix.data.remote.createPixelfedApi
import com.daniebeler.pfpixelix.data.repository.AccountRepositoryImpl
import com.daniebeler.pfpixelix.data.repository.AuthRepositoryImpl
import com.daniebeler.pfpixelix.data.repository.CollectionRepositoryImpl
import com.daniebeler.pfpixelix.data.repository.CountryRepositoryImpl
import com.daniebeler.pfpixelix.data.repository.DirectMessagesRepositoryImpl
import com.daniebeler.pfpixelix.data.repository.HashtagRepositoryImpl
import com.daniebeler.pfpixelix.data.repository.PostEditorRepositoryImpl
import com.daniebeler.pfpixelix.data.repository.PostRepositoryImpl
import com.daniebeler.pfpixelix.data.repository.SavedSearchesRepositoryImpl
import com.daniebeler.pfpixelix.data.repository.StorageRepositoryImpl
import com.daniebeler.pfpixelix.data.repository.TimelineRepositoryImpl
import com.daniebeler.pfpixelix.data.repository.WidgetRepositoryImpl
import com.daniebeler.pfpixelix.domain.model.AuthData
import com.daniebeler.pfpixelix.domain.model.SavedSearches
import com.daniebeler.pfpixelix.domain.repository.AccountRepository
import com.daniebeler.pfpixelix.domain.repository.AuthRepository
import com.daniebeler.pfpixelix.domain.repository.CollectionRepository
import com.daniebeler.pfpixelix.domain.repository.CountryRepository
import com.daniebeler.pfpixelix.domain.repository.DirectMessagesRepository
import com.daniebeler.pfpixelix.domain.repository.HashtagRepository
import com.daniebeler.pfpixelix.domain.repository.PostEditorRepository
import com.daniebeler.pfpixelix.domain.repository.PostRepository
import com.daniebeler.pfpixelix.domain.repository.SavedSearchesRepository
import com.daniebeler.pfpixelix.domain.repository.StorageRepository
import com.daniebeler.pfpixelix.domain.repository.TimelineRepository
import com.daniebeler.pfpixelix.domain.repository.WidgetRepository
import com.daniebeler.pfpixelix.utils.AuthDataSerializer
import com.daniebeler.pfpixelix.utils.KmpContext
import com.daniebeler.pfpixelix.utils.SavedSearchesSerializer
import com.daniebeler.pfpixelix.utils.coilContext
import com.daniebeler.pfpixelix.utils.dataStoreDir
import com.daniebeler.pfpixelix.utils.imageCacheDir
import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.converter.CallConverterFactory
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.plugin
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.KmpComponentCreate
import me.tatarka.inject.annotations.Provides
import me.tatarka.inject.annotations.Scope
import okio.FileSystem
import okio.SYSTEM

@Scope
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER
)
annotation class AppSingleton

@AppSingleton
@Component
abstract class AppComponent(
    @get:Provides val context: KmpContext
) {

    @Provides
    @AppSingleton
    fun provideUserDataStorePreferences(context: KmpContext): DataStore<Preferences> =
        PreferenceDataStoreFactory.createWithPath(
            corruptionHandler = null,
            migrations = emptyList(),
            produceFile = { context.dataStoreDir.resolve("user_datastore.preferences_pb") },
        )

    @Provides
    @AppSingleton
    fun provideDataStore(context: KmpContext): DataStore<SavedSearches> =
        DataStoreFactory.create(
            storage = OkioStorage(
                fileSystem = FileSystem.SYSTEM,
                producePath = { context.dataStoreDir.resolve("saved_searches.json") },
                serializer = SavedSearchesSerializer,
            )
        )

    @Provides
    @AppSingleton
    fun provideAuthDataStore(context: KmpContext): DataStore<AuthData> =
        DataStoreFactory.create(
            storage = OkioStorage(
                fileSystem = FileSystem.SYSTEM,
                producePath = { context.dataStoreDir.resolve("auth_data_datastore.json") },
                serializer = AuthDataSerializer,
            )
        )

    @Provides
    @AppSingleton
    fun provideHostSelectionInterceptor(): HostSelectionInterceptorInterface =
        HostSelectionInterceptor()

    @Provides
    @AppSingleton
    fun provideImageLoader(): ImageLoader =
        ImageLoader.Builder(context.coilContext)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .memoryCache(
                MemoryCache.Builder()
                    .maxSizePercent(context.coilContext, 0.2)
                    .build()
            )
            .diskCachePolicy(CachePolicy.ENABLED)
            .diskCache(
                DiskCache.Builder()
                    .maxSizeBytes(50L * 1024L * 1024L)
                    .directory(context.imageCacheDir)
                    .build()
            )
            .build()

    @Provides
    @AppSingleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        explicitNulls = false
    }

    @Provides
    @AppSingleton
    fun provideHttpClient(
        json: Json,
        hostSelectionInterceptor: HostSelectionInterceptorInterface
    ): HttpClient = HttpClient {
        install(ContentNegotiation) { json(json) }
        install(Logging) {
            logger = object : io.ktor.client.plugins.logging.Logger {
                override fun log(message: String) {
                    Logger.v("HttpClient") { message.lines().joinToString { "\n\t\t\t$it" } }
                }
            }
            level = LogLevel.BODY
        }
    }.apply {
        plugin(HttpSend).intercept { request ->
            with(hostSelectionInterceptor) {
                intercept(request)
            }
        }
    }

    @Provides
    @AppSingleton
    fun provideKtorfit(client: HttpClient): Ktorfit = Ktorfit.Builder()
        .converterFactories(CallConverterFactory())
        .httpClient(client)
        .baseUrl("https://err.or/")
        .build()

    @Provides
    @AppSingleton
    fun providePixelfedApi(ktorfit: Ktorfit): PixelfedApi = ktorfit.createPixelfedApi()

    @Provides
    fun getAccountRepository(impl: AccountRepositoryImpl): AccountRepository = impl

    @Provides
    fun getAuthRepository(impl: AuthRepositoryImpl): AuthRepository = impl

    @Provides
    fun getCollectionRepository(impl: CollectionRepositoryImpl): CollectionRepository = impl

    @Provides
    fun getCountryRepository(impl: CountryRepositoryImpl): CountryRepository = impl

    @Provides
    fun getDirectMessagesRepository(impl: DirectMessagesRepositoryImpl): DirectMessagesRepository =
        impl

    @Provides
    fun getHashtagRepository(impl: HashtagRepositoryImpl): HashtagRepository = impl

    @Provides
    fun getPostEditorRepository(impl: PostEditorRepositoryImpl): PostEditorRepository = impl

    @Provides
    fun getPostRepository(impl: PostRepositoryImpl): PostRepository = impl

    @Provides
    fun getSavedSearchesRepository(impl: SavedSearchesRepositoryImpl): SavedSearchesRepository =
        impl

    @Provides
    fun getStorageRepository(impl: StorageRepositoryImpl): StorageRepository = impl

    @Provides
    fun getTimelineRepository(impl: TimelineRepositoryImpl): TimelineRepository = impl

    @Provides
    fun getWidgetRepository(impl: WidgetRepositoryImpl): WidgetRepository = impl

    companion object
}

@KmpComponentCreate
expect fun AppComponent.Companion.create(context: KmpContext): AppComponent
