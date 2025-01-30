package com.daniebeler.pfpixelix.ui.composables.settings.muted_accounts

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniebeler.pfpixelix.common.Resource
import com.daniebeler.pfpixelix.domain.model.Account
import com.daniebeler.pfpixelix.domain.usecase.GetMutedAccountsUseCase
import com.daniebeler.pfpixelix.domain.usecase.UnmuteAccountUseCase
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.tatarka.inject.annotations.Inject

class MutedAccountsViewModel @Inject constructor(
    private val getMutedAccountsUseCase: GetMutedAccountsUseCase,
    private val unmuteAccountUseCase: UnmuteAccountUseCase
) : ViewModel() {


    var mutedAccountsState by mutableStateOf(MutedAccountsState())

    var unmuteAccountAlert: String by mutableStateOf("")

    init {
        getMutedAccounts()
    }

    fun getMutedAccounts(refreshing: Boolean = false) {
        getMutedAccountsUseCase().onEach { result ->
            mutedAccountsState = when (result) {
                is Resource.Success -> {
                    MutedAccountsState(mutedAccounts = result.data ?: emptyList())
                }

                is Resource.Error -> {
                    MutedAccountsState(error = result.message ?: "An unexpected error occurred")
                }

                is Resource.Loading -> {
                    MutedAccountsState(
                        isLoading = true,
                        isRefreshing = refreshing,
                        mutedAccounts = mutedAccountsState.mutedAccounts
                    )
                }
            }
        }.launchIn(viewModelScope)
    }

    fun unmuteAccount(accountId: String) {
        unmuteAccountUseCase(accountId).onEach { result ->
            mutedAccountsState = when (result) {
                is Resource.Success -> {
                    val newMutedAccounts =
                        mutedAccountsState.mutedAccounts.filter { account: Account -> account.id != accountId }
                    MutedAccountsState(mutedAccounts = newMutedAccounts)
                }

                is Resource.Error -> {
                    mutedAccountsState.copy(error = result.message ?: "An unexpected error occurred")
                }

                is Resource.Loading -> {
                    mutedAccountsState.copy(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

}