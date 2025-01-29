package com.daniebeler.pfpixelix.data.remote.dto.nodeinfo

import com.daniebeler.pfpixelix.data.remote.dto.DtoInterface
import com.daniebeler.pfpixelix.domain.model.nodeinfo.FediServer
import com.daniebeler.pfpixelix.domain.model.nodeinfo.ServerStats
import com.daniebeler.pfpixelix.domain.model.nodeinfo.SoftwareSmall
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WrapperDto(
    @SerialName("data") val data: FediServerDto
) : DtoInterface<FediServer> {
    override fun toModel(): FediServer {
        return FediServer(
            bannerUrl = data.bannerUrl ?: "",
            description = data.description ?: "",
            domain = data.domain ?: "",
            id = data.id,
            openRegistration = data.openRegistration ?: false,
            software = data.software?.toModel() ?: SoftwareSmall(-1, "", "", ""),
            stats = data.stats?.toModel() ?: ServerStats(-1, -1, -1)
        )
    }
}
