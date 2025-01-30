package com.daniebeler.pfpixelix.ui.composables.settings.bookmarked_posts

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniebeler.pfpixelix.common.Resource
import com.daniebeler.pfpixelix.domain.usecase.GetBookmarkedPostsUseCase
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.tatarka.inject.annotations.Inject

class BookmarkedPostsViewModel @Inject constructor(
    private val getBookmarkedPostsUseCase: GetBookmarkedPostsUseCase
) : ViewModel() {

    var bookmarkedPostsState by mutableStateOf(BookmarkedPostsState())

    init {
        getBookmarkedPosts()
    }

    fun getBookmarkedPosts(refreshing: Boolean = false) {
        getBookmarkedPostsUseCase().onEach { result ->
            bookmarkedPostsState = when (result) {
                is Resource.Success -> {
                    BookmarkedPostsState(bookmarkedPosts = result.data ?: emptyList())
                }

                is Resource.Error -> {
                    BookmarkedPostsState(error = result.message ?: "An unexpected error occurred")
                }

                is Resource.Loading -> {
                    BookmarkedPostsState(isLoading = true, isRefreshing = refreshing)
                }
            }
        }.launchIn(viewModelScope)
    }

}