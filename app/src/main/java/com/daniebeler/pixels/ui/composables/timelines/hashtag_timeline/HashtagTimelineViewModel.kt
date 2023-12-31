package com.daniebeler.pixels.ui.composables.timelines.hashtag_timeline

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniebeler.pixels.common.Resource
import com.daniebeler.pixels.domain.repository.CountryRepository
import com.daniebeler.pixels.domain.model.Post
import com.daniebeler.pixels.ui.composables.trending.trending_posts.TrendingPostsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HashtagTimelineViewModel @Inject constructor(
    private val repository: CountryRepository
): ViewModel() {

    var postsState by mutableStateOf(HashtagTimelineState())
    var hashtagState by mutableStateOf(HashtagState())

    fun getHashtagTimeline(hashtag: String) {
        repository.getHashtagTimeline(hashtag).onEach { result ->
            postsState = when (result) {
                is Resource.Success -> {
                    HashtagTimelineState(hashtagTimeline = result.data ?: emptyList())
                }

                is Resource.Error -> {
                    HashtagTimelineState(error = result.message ?: "An unexpected error occurred")
                }

                is Resource.Loading -> {
                    HashtagTimelineState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun getHashtagInfo(hashtag: String) {
        repository.getHashtag(hashtag).onEach { result ->
            hashtagState = when (result) {
                is Resource.Success -> {
                    HashtagState(hashtag = result.data)
                }

                is Resource.Error -> {
                    HashtagState(error = result.message ?: "An unexpected error occurred")
                }

                is Resource.Loading -> {
                    HashtagState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun followHashtag(hashtag: String) {
        repository.followHashtag(hashtag).onEach { result ->
            hashtagState = when (result) {
                is Resource.Success -> {
                    HashtagState(hashtag = result.data)
                }

                is Resource.Error -> {
                    HashtagState(error = result.message ?: "An unexpected error occurred")
                }

                is Resource.Loading -> {
                    HashtagState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun unfollowHashtag(hashtag: String) {
        repository.unfollowHashtag(hashtag).onEach { result ->
            hashtagState = when (result) {
                is Resource.Success -> {
                    HashtagState(hashtag = result.data)
                }

                is Resource.Error -> {
                    HashtagState(error = result.message ?: "An unexpected error occurred")
                }

                is Resource.Loading -> {
                    HashtagState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }
}