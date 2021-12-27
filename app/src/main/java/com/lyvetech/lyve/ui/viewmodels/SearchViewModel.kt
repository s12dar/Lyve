package com.lyvetech.lyve.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lyvetech.lyve.datamanager.DataManager
import com.lyvetech.lyve.models.Activity
import com.lyvetech.lyve.models.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val dataManager: DataManager
) : ViewModel() {
    val currentUser = dataManager.getCurrentUser()

    fun searchActivities(searchQuery: String): LiveData<List<Activity>> {
        return dataManager.getSearchedActivities(searchQuery)
    }

    fun searchUsers(searchQuery: String): LiveData<List<User>> {
        return dataManager.getSearchedUsers(searchQuery)
    }

    fun updateUser(user: User) = viewModelScope.launch {
        dataManager.updateUser(user)
    }
}