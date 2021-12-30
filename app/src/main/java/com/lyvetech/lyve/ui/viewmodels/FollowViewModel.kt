package com.lyvetech.lyve.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lyvetech.lyve.datamanager.DataManager
import com.lyvetech.lyve.models.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FollowViewModel @Inject constructor(
    private val dataManager: DataManager
) : ViewModel() {
    val currentUser = dataManager.getCurrentUser()

    fun getFollowings(user: User): LiveData<List<User>> {
        return dataManager.getFollowings(user)
    }

    fun getFollowers(user: User): LiveData<List<User>> {
        return dataManager.getFollowers(user)
    }

    fun updateUser(user: User) = viewModelScope.launch {
        dataManager.updateUser(user)
    }
}