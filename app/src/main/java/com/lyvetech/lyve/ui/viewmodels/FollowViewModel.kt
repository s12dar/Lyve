package com.lyvetech.lyve.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lyvetech.lyve.models.User
import com.lyvetech.lyve.repositories.LyveRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FollowViewModel @Inject constructor(
    private val lyveRepository: LyveRepository
) : ViewModel() {
//    val currentUser = lyveRepository.getCurrentUser()
//
//    fun getFollowings(user: User): LiveData<List<User>> {
//        return lyveRepository.getFollowings(user)
//    }
//
//    fun getFollowers(user: User): LiveData<List<User>> {
//        return lyveRepository.getFollowers(user)
//    }
//
//    fun updateUser(user: User) = viewModelScope.launch {
//        lyveRepository.updateUser(user)
//    }
}