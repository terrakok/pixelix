package com.daniebeler.pfpixelix.ui.composables.explore.trending.trending_accounts

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniebeler.pfpixelix.common.Resource
import com.daniebeler.pfpixelix.domain.usecase.GetPostsOfAccountUseCase
import com.daniebeler.pfpixelix.domain.usecase.OpenExternalUrlUseCase
import com.daniebeler.pfpixelix.utils.KmpContext
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.tatarka.inject.annotations.Inject

class TrendingAccountElementViewModel @Inject constructor(
    private val getAccountPosts: GetPostsOfAccountUseCase,
    private val openExternalUrlUseCase: OpenExternalUrlUseCase
) : ViewModel() {
    var postsState by mutableStateOf(TrendingAccountPostsState())

    fun loadItems(accountId: String) {
        if (postsState.posts.isEmpty()) {
            getAccountPosts(accountId, limit = 9).onEach { result ->
                postsState = when (result) {
                    is Resource.Success -> {
                        TrendingAccountPostsState(
                            posts = result.data ?: emptyList(), error = "", isLoading = false
                        )
                    }

                    is Resource.Error -> {
                        TrendingAccountPostsState(
                            posts = postsState.posts,
                            error = result.message ?: "An unexpected error occurred",
                            isLoading = false
                        )
                    }

                    is Resource.Loading -> {
                        TrendingAccountPostsState(
                            posts = postsState.posts, error = "", isLoading = true
                        )
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun openUrl(context: KmpContext, url: String) {
        openExternalUrlUseCase(context, url)
    }
}