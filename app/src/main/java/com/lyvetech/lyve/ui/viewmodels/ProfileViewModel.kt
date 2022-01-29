package com.lyvetech.lyve.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.lyvetech.lyve.repositories.DefaultLyveRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val lyveRepository: DefaultLyveRepository
) : ViewModel() {
    val currentUser = lyveRepository.getCurrentUser()
}