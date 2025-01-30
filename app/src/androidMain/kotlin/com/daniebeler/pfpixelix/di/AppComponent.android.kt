package com.daniebeler.pfpixelix.di

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.daniebeler.pfpixelix.domain.model.AuthData
import com.daniebeler.pfpixelix.domain.model.SavedSearches
import com.daniebeler.pfpixelix.utils.AuthDataSerializer
import com.daniebeler.pfpixelix.utils.KmpContext
import com.daniebeler.pfpixelix.utils.SavedSearchesSerializer
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides


@Component
abstract class AndroidAppComponent(
    @get:Provides val app: Application,
    @Component val appComponent: AppComponent
)

internal actual fun createAuthDataStore(context: KmpContext): DataStore<AuthData> =
    DataStoreFactory.create(serializer = AuthDataSerializer(),
        produceFile = { context.applicationContext.dataStoreFile("auth_data_datastore.json") })

internal actual fun createSavedSearchesDataStore(context: KmpContext): DataStore<SavedSearches> =
    DataStoreFactory.create(serializer = SavedSearchesSerializer(),
        produceFile = { context.applicationContext.dataStoreFile("saved_searches.json") })


private val Context.dataStore by preferencesDataStore("settings")
internal actual fun createPreferencesDataStore(context: KmpContext): DataStore<Preferences> =
    context.applicationContext.dataStore