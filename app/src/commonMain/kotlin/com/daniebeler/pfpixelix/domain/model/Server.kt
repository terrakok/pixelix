package com.daniebeler.pfpixelix.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Server(
    @SerialName("header_thumbnail") val headerThumbnail: String?,
    @SerialName("domain") val domain: String,
    @SerialName("mobile_registration") val mobileRegistrations: Boolean?,
    @SerialName("version") val version: String,
    @SerialName("short_description") val shortDescription: String?,
    @SerialName("user_count") val userCount: Int,
    @SerialName("last_seen_at") val lastSeenAt: String
)