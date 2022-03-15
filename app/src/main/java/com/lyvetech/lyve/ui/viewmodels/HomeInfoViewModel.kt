package com.lyvetech.lyve.ui.viewmodels

import androidx.lifecycle.*
import com.lyvetech.lyve.di.IoDispatcher
import com.lyvetech.lyve.models.Activity
import com.lyvetech.lyve.models.User
import com.lyvetech.lyve.repositories.LyveRepository
import com.lyvetech.lyve.utils.Resource
import com.lyvetech.lyve.utils.SimpleResource
import com.lyvetech.lyve.utils.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@HiltViewModel
class HomeInfoViewModel @Inject constructor(
    private val repository: LyveRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val coroutineContext = viewModelScope.coroutineContext + ioDispatcher

    private val _isLoading = MutableLiveData<Boolean>()
    private val isLoading = _isLoading.asLiveData()

    private val _dataFetchedState = MutableLiveData<Boolean>()
    private val dataFetchedState = _dataFetchedState.asLiveData()

    fun getActivities(): LiveData<Resource<List<Activity>>> =
        liveData(coroutineContext) {
            emit(Resource.Loading())

            when (val result = repository.getActivities()) {
                is Resource.Success -> {
                    _isLoading.value = false
                    if (result.data != null) {
                        _dataFetchedState.value = true
                        emit(Resource.Success(data = result.data))
                    } else {
                        _dataFetchedState.value = false
                        emit(
                            Resource.Error(
                                data = result.data,
                                message = "No activities found, it's null"
                            )
                        )
                    }
                }
                is Resource.Error -> {
                    _isLoading.value = false
                    _dataFetchedState.value = false
                    emit(Resource.Error(message = result.message))
                }
                is Resource.Loading -> {
                    _isLoading.value = true
                    _dataFetchedState.value = false
                    emit(Resource.Loading(data = result.data))
                }
            }
        }

    fun updateActivity(activity: Activity, user: User): LiveData<SimpleResource> =
        liveData(coroutineContext) {
            emit(Resource.Loading())

            when (val result = repository.updateActivity(activity, user)) {
                is Resource.Success -> {
                    _isLoading.value = false
                    _dataFetchedState.value = true
                    emit(Resource.Success(Unit))
                }
                is Resource.Error -> {
                    _isLoading.value = false
                    _dataFetchedState.value = false
                    emit(Resource.Error(message = result.message))
                }
                is Resource.Loading -> {
                    _isLoading.value = true
                    _dataFetchedState.value = false
                    emit(Resource.Loading(Unit))
                }

            }
        }
}