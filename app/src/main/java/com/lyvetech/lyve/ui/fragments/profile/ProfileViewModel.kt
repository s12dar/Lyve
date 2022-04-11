package com.lyvetech.lyve.ui.fragments.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lyvetech.lyve.di.IoDispatcher
import com.lyvetech.lyve.repositories.LyveRepository
import com.lyvetech.lyve.utils.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val lyveRepository: LyveRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val coroutineContext = viewModelScope.coroutineContext + ioDispatcher

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading = _isLoading.asLiveData()

    private val _dataFetchState = MutableLiveData<Boolean>()
    val dataFetchState = _dataFetchState.asLiveData()
}