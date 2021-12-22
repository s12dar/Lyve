package com.lyvetech.lyve.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.lyvetech.lyve.datamanager.DataManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val dataManager: DataManager
) : ViewModel() {

    val currentUser = dataManager.getCurrentUser()
}