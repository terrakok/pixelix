package com.daniebeler.pfpixelix.domain.usecase

import com.daniebeler.pfpixelix.common.Constants
import com.daniebeler.pfpixelix.common.Resource
import com.daniebeler.pfpixelix.domain.model.Post
import com.daniebeler.pfpixelix.domain.repository.StorageRepository
import com.daniebeler.pfpixelix.domain.repository.TimelineRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import me.tatarka.inject.annotations.Inject

@Inject
class GetHashtagTimelineUseCase(
    private val timelineRepository: TimelineRepository,
    private val storageRepository: StorageRepository
) {
    operator fun invoke(hashtag: String, maxPostId: String = "", limit: Int = Constants.HASHTAG_TIMELINE_POSTS_LIMIT): Flow<Resource<List<Post>>> =
        flow {
            emit(Resource.Loading())
            val hideSensitiveContent = storageRepository.getHideSensitiveContent().first()
            timelineRepository.getHashtagTimeline(hashtag, maxPostId, limit).collect { timeline ->
                if (timeline is Resource.Success && hideSensitiveContent) {
                    val res: List<Post> = timeline.data?.filter { s -> !s.sensitive } ?: emptyList()
                    emit(Resource.Success(res))
                } else {
                    emit(timeline)
                }
            }
        }
}