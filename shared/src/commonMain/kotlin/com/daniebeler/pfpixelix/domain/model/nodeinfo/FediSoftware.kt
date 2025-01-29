package com.daniebeler.pfpixelix.domain.model.nodeinfo

data class FediSoftware(
    val description: String,
    val id: Int,
    val instanceCount: Int,
    val license: String,
    val name: String,
    val slug: String,
    val statusCount: Int,
    val userCount: Int,
    val activeUserCount: Int,
//    @DrawableRes todo
    var icon: Int?,
    val website: String
)