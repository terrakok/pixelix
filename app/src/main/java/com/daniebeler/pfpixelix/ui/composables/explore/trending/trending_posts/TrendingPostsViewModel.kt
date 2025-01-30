package com.daniebeler.pfpixelix.ui.composables.explore.trending.trending_posts

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniebeler.pfpixelix.common.Resource
import com.daniebeler.pfpixelix.domain.usecase.GetTrendingPostsUseCase
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.tatarka.inject.annotations.Inject

class TrendingPostsViewModel @Inject constructor(
    private val getTrendingPostsUseCase: GetTrendingPostsUseCase
) : ViewModel() {

    var trendingState by mutableStateOf(TrendingPostsState())
        private set

    fun getTrendingPosts(timeRange: String, refreshing: Boolean = false) {
        getTrendingPostsUseCase(timeRange).onEach { result ->
            trendingState = when (result) {
                is Resource.Success -> {
                    TrendingPostsState(trendingPosts = result.data ?: emptyList())
                }

                is Resource.Error -> {
                    TrendingPostsState(error = result.message ?: "An unexpected error occurred")
                }

                is Resource.Loading -> {
                    TrendingPostsState(
                        isLoading = true,
                        isRefreshing = refreshing,
                        trendingPosts = emptyList()
                    )
                }
            }
        }.launchIn(viewModelScope)
    }
}