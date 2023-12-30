package com.daniebeler.pixels.ui.composables.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daniebeler.pixels.domain.model.Account
import com.daniebeler.pixels.domain.repository.CountryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MutedAccountsViewModel @Inject constructor(
    private val repository: CountryRepository
): ViewModel() {


    var mutedAccounts: List<Account> by mutableStateOf(emptyList())

    var unmuteAlert: String by mutableStateOf("")

    init {
        viewModelScope.launch {
            mutedAccounts = repository.getMutedAccounts()
        }
    }

    fun unmuteAccount(accountId: String) {
        viewModelScope.launch {
            var res = repository.unmuteAccount(accountId)
            if (res != null) {
                mutedAccounts = repository.getMutedAccounts()
            }
        }
    }

}