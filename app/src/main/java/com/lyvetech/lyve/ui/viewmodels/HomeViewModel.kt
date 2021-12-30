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
class HomeViewModel @Inject constructor(
    private val dataManager: DataManager
) : ViewModel() {
    val currentUser = dataManager.getCurrentUser()
    val allActivities = dataManager.getActivities()
    val allUsers = dataManager.getUsers()

    fun createActivity(activity: Activity, user: User) = viewModelScope.launch {
        dataManager.createActivity(activity, user)
    }

    fun getFollowingActivities(user: User): LiveData<List<Activity>> {
        return dataManager.getFollowingActivities(user)
    }
}