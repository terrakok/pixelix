package com.daniebeler.pfpixelix.domain.usecase.nodeinfo

import com.daniebeler.pfpixelix.common.Resource
import com.daniebeler.pfpixelix.domain.model.nodeinfo.FediSoftware
import com.daniebeler.pfpixelix.domain.repository.CountryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import me.tatarka.inject.annotations.Inject
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.*

@Inject
class GetFediSoftwareUseCase(private val countryRepository: CountryRepository) {
    operator fun invoke(domain: String): Flow<Resource<FediSoftware>> = flow {
        emit(Resource.Loading())
        countryRepository.getSoftwareFromFediDB(domain).collect { res ->
            if (res is Resource.Error) {
                println("fief fief")
                emit(Resource.Error(res.message!!))
            }
            if (res is Resource.Success) {
                println("fief")
                res.data.icon = getSlugIcon(res.data.slug)
                emit(res)
            }
        }
    }

    private fun getSlugIcon(slug: String) = when (slug) {
        "pixelfed" -> Res.drawable.pixelfed_logo
        "mastodon" -> Res.drawable.mastodon_logo
        "peertube" -> Res.drawable.peertube_logo
        "lemmy" -> Res.drawable.lemmy_logo
        "misskey" -> Res.drawable.misskey_logo
        else -> Res.drawable.fediverse_logo
    }
}

