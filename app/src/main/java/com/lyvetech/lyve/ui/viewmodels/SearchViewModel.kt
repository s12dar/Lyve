package com.lyvetech.lyve.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lyvetech.lyve.models.Activity
import com.lyvetech.lyve.models.User
import com.lyvetech.lyve.repositories.LyveRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val lyveRepository: LyveRepository
) : ViewModel() {
    val currentUser = lyveRepository.getCurrentUser()

    fun searchActivities(searchQuery: String): LiveData<List<Activity>> {
        return lyveRepository.getSearchedActivities(searchQuery)
    }

    fun searchUsers(searchQuery: String): LiveData<List<User>> {
        return lyveRepository.getSearchedUsers(searchQuery)
    }

    fun updateUser(user: User) = viewModelScope.launch {
        lyveRepository.updateUser(user)
    }
}