package com.tech.lyve.ui.fragments.follow

import androidx.lifecycle.*
import com.tech.lyve.di.IoDispatcher
import com.tech.lyve.models.User
import com.tech.lyve.repositories.LyveRepository
import com.tech.lyve.utils.Resource
import com.tech.lyve.utils.SimpleResource
import com.tech.lyve.utils.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@HiltViewModel
class FollowViewModel @Inject constructor(
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

    fun getFollowings(user: User): LiveData<Resource<List<User>>> =
        liveData(coroutineContext) {
            emit(Resource.Loading())

            when (val result = repository.getFollowings(user)) {
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
                                message = "No Followings found, it's null"
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

    fun getFollowers(user: User): LiveData<Resource<List<User>>> =
        liveData(coroutineContext) {
            emit(Resource.Loading())

            when (val result = repository.getFollowings(user)) {
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
                                message = "No Followers found, it's null"
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

    fun updateUser(user: User): LiveData<SimpleResource> =
        liveData(coroutineContext) {
            emit(Resource.Loading())

            when (val result = repository.updateUser(user)) {
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