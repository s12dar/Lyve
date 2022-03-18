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
class OnboardingViewModel @Inject constructor(
    private val repository: LyveRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val coroutineContext = viewModelScope.coroutineContext + ioDispatcher

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading = _isLoading.asLiveData()

    private val _dataFetchState = MutableLiveData<Boolean>()
    val dataFetchState = _dataFetchState.asLiveData()

    fun createUser(user: User): LiveData<SimpleResource> =
        liveData(coroutineContext) {
            emit(Resource.Loading())

            when (val result = repository.createUser(user)) {
                is Resource.Success -> {
                    _isLoading.value = false
                    _dataFetchState.value = true
                    emit(Resource.Success(Unit))
                }
                is Resource.Error -> {
                    _isLoading.value = false
                    _dataFetchState.value = false
                    emit(Resource.Error(message = result.message))
                }
                is Resource.Loading -> {
                    _isLoading.value = true
                    _dataFetchState.value = false
                    emit(Resource.Loading(Unit))
                }
            }
        }
}