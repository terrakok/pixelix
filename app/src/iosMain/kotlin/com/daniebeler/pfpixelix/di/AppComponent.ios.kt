package com.daniebeler.pfpixelix.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.daniebeler.pfpixelix.domain.model.AuthData
import com.daniebeler.pfpixelix.domain.model.SavedSearches
import com.daniebeler.pfpixelix.utils.KmpContext

internal actual fun createAuthDataStore(context: KmpContext): DataStore<AuthData> {
    TODO("Not yet implemented")
}

internal actual fun createSavedSearchesDataStore(context: KmpContext): DataStore<SavedSearches> {
    TODO("Not yet implemented")
}

internal actual fun createPreferencesDataStore(context: KmpContext): DataStore<Preferences> {
    TODO("Not yet implemented")
}