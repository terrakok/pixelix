package com.daniebeler.pfpixelix.ui.composables.explore.trending.trending_hashtags

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniebeler.pfpixelix.common.Resource
import com.daniebeler.pfpixelix.domain.usecase.GetTrendingHashtagsUseCase
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.tatarka.inject.annotations.Inject

class TrendingHashtagsViewModel @Inject constructor(
    private val getTrendingHashtagsUseCase: GetTrendingHashtagsUseCase
) : ViewModel() {

    var trendingHashtagsState by mutableStateOf(TrendingHashtagsState())

    init {
        getTrendingHashtags()
    }

    fun getTrendingHashtags(refreshing: Boolean = false) {
        getTrendingHashtagsUseCase().onEach { result ->
            trendingHashtagsState = when (result) {
                is Resource.Success -> {
                    TrendingHashtagsState(trendingHashtags = result.data ?: emptyList())
                }

                is Resource.Error -> {
                    TrendingHashtagsState(error = result.message ?: "An unexpected error occurred")
                }

                is Resource.Loading -> {
                    TrendingHashtagsState(
                        isLoading = true,
                        isRefreshing = refreshing,
                        trendingHashtags = trendingHashtagsState.trendingHashtags
                    )
                }
            }
        }.launchIn(viewModelScope)
    }
}