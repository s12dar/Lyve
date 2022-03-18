package com.lyvetech.lyve.ui.viewmodels

import androidx.lifecycle.*
import com.lyvetech.lyve.di.IoDispatcher
import com.lyvetech.lyve.models.User
import com.lyvetech.lyve.repositories.LyveRepository
import com.lyvetech.lyve.utils.Resource
import com.lyvetech.lyve.utils.SimpleResource
import com.lyvetech.lyve.utils.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: LyveRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val coroutineContext = viewModelScope.coroutineContext + ioDispatcher

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading = _isLoading.asLiveData()

    private val _dataState = MutableLiveData<Boolean>()
    val dataState = _dataState.asLiveData()

    fun loginUser(user: User): LiveData<SimpleResource> =
        liveData(coroutineContext) {
            emit(Resource.Loading())

            when (val result = repository.loginUser(user)) {
                is Resource.Success -> {
                    _isLoading.value = false
                    _dataState.value = true
                    emit(Resource.Success(Unit))
                }
                is Resource.Error -> {
                    _isLoading.value = false
                    _dataState.value = false
                    emit(Resource.Error(message = result.message))
                }
                is Resource.Loading -> {
                    _isLoading.value = true
                    _dataState.value = false
                    emit(Resource.Loading(Unit))
                }
            }
        }
}