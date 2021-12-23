package com.lyvetech.lyve.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.lyvetech.lyve.datamanager.DataManager
import com.lyvetech.lyve.models.Activity
import com.lyvetech.lyve.models.User
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val dataManager: DataManager
) : ViewModel() {
    fun searchActivities(searchQuery: String): LiveData<List<Activity?>?> {
        return dataManager.getSearchedActivities(searchQuery)
    }

    fun searchUsers(searchQuery: String): LiveData<List<User?>?> {
        return dataManager.getSearchedUsers(searchQuery)
    }
}