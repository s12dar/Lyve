package com.lyvetech.lyve.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lyvetech.lyve.models.User
import com.lyvetech.lyve.repositories.LyveRepository
import com.lyvetech.lyve.repositories.LyveRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val lyveRepository: LyveRepository
) : ViewModel() {
    fun createUser(user: User) = viewModelScope.launch {
        lyveRepository.createUser(user)
    }
}