package com.daniebeler.pfpixelix.utils

import androidx.datastore.core.okio.OkioSerializer
import com.daniebeler.pfpixelix.domain.model.SavedSearches
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import okio.BufferedSink
import okio.BufferedSource

object SavedSearchesSerializer: OkioSerializer<SavedSearches> {
    override val defaultValue: SavedSearches
        get() = SavedSearches()

    override suspend fun readFrom(source: BufferedSource): SavedSearches {
        return try {
            Json.decodeFromString(
                deserializer = SavedSearches.serializer(),
                string = source.readUtf8()
            )
        } catch (e: SerializationException) {
            e.printStackTrace();
            defaultValue
        }
    }

    override suspend fun writeTo(t: SavedSearches, sink: BufferedSink) {
        sink.write(
            Json.encodeToString(
                serializer = SavedSearches.serializer(),
                value = t
            ).encodeToByteArray()
        )
    }
}