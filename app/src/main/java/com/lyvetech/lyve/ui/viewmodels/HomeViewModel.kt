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
class HomeViewModel @Inject constructor(
    private val lyveRepository: LyveRepository
) : ViewModel() {
    val currentUser = lyveRepository.getCurrentUser()
    val allActivities = lyveRepository.getActivities()
    val allUsers = lyveRepository.getUsers()

    fun createActivity(activity: Activity, user: User) = viewModelScope.launch {
        lyveRepository.createActivity(activity, user)
    }

    fun getFollowingActivities(user: User): LiveData<List<Activity>> {
        return lyveRepository.getFollowingActivities(user)
    }
}