package com.daniebeler.pfpixelix.di

import HostSelectionInterceptor
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
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
import com.daniebeler.pfpixelix.utils.SavedSearchesSerializer
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
import me.tatarka.inject.annotations.Provides
import me.tatarka.inject.annotations.Scope


private val Context.dataStore by preferencesDataStore("settings")

@Scope
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER)
annotation class AppSingleton

@AppSingleton
@Component
abstract class Module(
    @get:Provides val app: Application
) {

    @Provides
    @AppSingleton
    fun provideContext(): Context = app

    @Provides
    @AppSingleton
    fun provideUserDataStorePreferences(context: Context): DataStore<Preferences> {
        return context.applicationContext.dataStore
    }


    @Provides
    @AppSingleton
    fun provideDataStore(context: Context): DataStore<SavedSearches> =
        DataStoreFactory.create(serializer = SavedSearchesSerializer(),
            produceFile = { context.applicationContext.dataStoreFile("saved_searches.json") })


    @Provides
    @AppSingleton
    fun provideAuthDataStore(context: Context): DataStore<AuthData> =
        DataStoreFactory.create(serializer = AuthDataSerializer(),
            produceFile = { context.applicationContext.dataStoreFile("auth_data_datastore.json") })


    @Provides
    @AppSingleton
    fun provideHostSelectionInterceptor(): HostSelectionInterceptorInterface =
        HostSelectionInterceptor()


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
                    Log.v("HttpClient", message.lines().joinToString { "\n\t\t\t$it"} )
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
    fun providePixelfedApi(ktorfit: Ktorfit): PixelfedApi =
        ktorfit.createPixelfedApi()

    @Provides
    fun getAccountRepository(impl: AccountRepositoryImpl): AccountRepository = impl
    @Provides
    fun getAuthRepository(impl: AuthRepositoryImpl): AuthRepository = impl
    @Provides
    fun getCollectionRepository(impl: CollectionRepositoryImpl): CollectionRepository = impl
    @Provides
    fun getCountryRepository(impl: CountryRepositoryImpl): CountryRepository = impl
    @Provides
    fun getDirectMessagesRepository(impl: DirectMessagesRepositoryImpl): DirectMessagesRepository = impl
    @Provides
    fun getHashtagRepository(impl: HashtagRepositoryImpl): HashtagRepository = impl
    @Provides
    fun getPostEditorRepository(impl: PostEditorRepositoryImpl): PostEditorRepository = impl
    @Provides
    fun getPostRepository(impl: PostRepositoryImpl): PostRepository = impl
    @Provides
    fun getSavedSearchesRepository(impl: SavedSearchesRepositoryImpl): SavedSearchesRepository = impl
    @Provides
    fun getStorageRepository(impl: StorageRepositoryImpl): StorageRepository = impl
    @Provides
    fun getTimelineRepository(impl: TimelineRepositoryImpl): TimelineRepository = impl
    @Provides
    fun getWidgetRepository(impl: WidgetRepositoryImpl): WidgetRepository = impl
}