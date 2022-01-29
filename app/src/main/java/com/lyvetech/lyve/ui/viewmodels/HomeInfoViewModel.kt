package com.lyvetech.lyve.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lyvetech.lyve.repositories.LyveRepository
import com.lyvetech.lyve.models.Activity
import com.lyvetech.lyve.models.User
import com.lyvetech.lyve.repositories.DefaultLyveRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeInfoViewModel @Inject constructor(
    private val lyveRepository: DefaultLyveRepository
) : ViewModel() {
    val allActivities = lyveRepository.getActivities()

    fun updateActivity(activity: Activity, user: User) = viewModelScope.launch {
        lyveRepository.updateActivity(activity, user)
    }
}