package com.lyvetech.lyve.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.lyvetech.lyve.repositories.LyveRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val lyveRepository: LyveRepository
) : ViewModel() {
    val currentUser = lyveRepository.getCurrentUser()
}