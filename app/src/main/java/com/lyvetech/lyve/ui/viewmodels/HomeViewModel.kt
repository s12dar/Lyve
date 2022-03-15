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
class HomeViewModel @Inject constructor(
    private val repository: LyveRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val coroutineContext = viewModelScope.coroutineContext + ioDispatcher

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading = _isLoading.asLiveData()

    private val _dataFetchState = MutableLiveData<Boolean>()
    val dataFetchState = _dataFetchState.asLiveData()

    fun getCurrentUser(): LiveData<Resource<User>> =
        liveData(coroutineContext) {
            emit(Resource.Loading())

            when (val result = repository.getCurrentUser()) {
                is Resource.Success -> {
                    _isLoading.value = false
                    if (result.data != null) {
                        _dataFetchState.value = true
                        emit(Resource.Success(data = result.data))
                    } else {
                        _dataFetchState.value = false
                        emit(
                            Resource.Error(
                                data = result.data,
                                "Current user is not found, it is null"
                            )
                        )
                    }
                }
                is Resource.Error -> {
                    _isLoading.value = false
                    _dataFetchState.value = false
                    emit(Resource.Error(message = result.message))
                }
                is Resource.Loading -> {
                    _isLoading.value = true
                    _dataFetchState.value = false
                    emit(Resource.Loading(data = result.data))
                }
            }
        }

    fun getActivities(): LiveData<Resource<List<Activity>>> =
        liveData(coroutineContext) {
            emit(Resource.Loading())

            when (val result = repository.getActivities()) {
                is Resource.Success -> {
                    _isLoading.value = false
                    if (result.data != null) {
                        _dataFetchState.value = true
                        emit(Resource.Success(data = result.data))
                    } else {
                        _dataFetchState.value = false
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
                    _dataFetchState.value = false
                    emit(Resource.Error(message = result.message))
                }
                is Resource.Loading -> {
                    _isLoading.value = true
                    _dataFetchState.value = false
                    emit(Resource.Loading(data = result.data))
                }
            }
        }

    fun getUsers(): LiveData<Resource<List<User>>> =
        liveData(coroutineContext) {
            emit(Resource.Loading())

            when (val result = repository.getUsers()) {
                is Resource.Success -> {
                    _isLoading.value = false
                    if (result.data != null) {
                        _dataFetchState.value = true
                        emit(Resource.Success(data = result.data))
                    } else {
                        _dataFetchState.value = false
                        emit(
                            Resource.Error(
                                data = result.data,
                                message = "No Users found, it's null"
                            )
                        )
                    }
                }
                is Resource.Error -> {
                    _isLoading.value = false
                    _dataFetchState.value = false
                    emit(Resource.Error(message = result.message))
                }
                is Resource.Loading -> {
                    _isLoading.value = true
                    _dataFetchState.value = false
                    emit(Resource.Loading(data = result.data))
                }
            }
        }

    fun createActivity(activity: Activity, user: User): LiveData<SimpleResource> =
        liveData(coroutineContext) {
            emit(Resource.Loading())

            when (val result = repository.createActivity(activity, user)) {
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

    fun getFollowingActivities(user: User): LiveData<Resource<List<Activity>>> =
        liveData(coroutineContext) {
            emit(Resource.Loading())

            when (val result = repository.getFollowingActivities(user)) {
                is Resource.Success -> {
                    _isLoading.value = false
                    if (result.data != null) {
                        _dataFetchState.value = true
                        emit(Resource.Success(data = result.data))
                    } else {
                        _dataFetchState.value = false
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
                    _dataFetchState.value = false
                    emit(Resource.Error(message = result.message))
                }
                is Resource.Loading -> {
                    _isLoading.value = true
                    _dataFetchState.value = false
                    emit(Resource.Loading(data = result.data))
                }
            }
        }
}