package com.lyvetech.lyve.ui.fragments.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.lyvetech.lyve.di.IoDispatcher
import com.lyvetech.lyve.models.Event
import com.lyvetech.lyve.models.User
import com.lyvetech.lyve.repositories.LyveRepository
import com.lyvetech.lyve.utils.Resource
import com.lyvetech.lyve.utils.SimpleResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: LyveRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val coroutineContext = viewModelScope.coroutineContext + ioDispatcher

    fun getCurrentUser(): LiveData<Resource<User>> =
        liveData(coroutineContext) {
            emit(Resource.Loading())

            when (val result = repository.getCurrentUser()) {
                is Resource.Success -> {
                    if (result.data != null) {
                        emit(Resource.Success(data = result.data))
                    } else {
                        emit(
                            Resource.Error(
                                data = result.data,
                                "Current user is not found, it is null"
                            )
                        )
                    }
                }
                is Resource.Error -> {
                    emit(Resource.Error(message = result.message))
                }
                is Resource.Loading -> {
                    emit(Resource.Loading(data = result.data))
                }
            }
        }

    fun getActivities(): LiveData<Resource<List<Event>>> =
        liveData(coroutineContext) {
            emit(Resource.Loading())

            when (val result = repository.getActivities()) {
                is Resource.Success -> {
                    if (result.data != null) {
                        emit(Resource.Success(data = result.data))
                    } else {
                        emit(
                            Resource.Error(
                                data = result.data,
                                message = "No activities found, it's null"
                            )
                        )
                    }
                }
                is Resource.Error -> {
                    emit(Resource.Error(message = result.message))
                }
                is Resource.Loading -> {
                    emit(Resource.Loading(data = result.data))
                }
            }
        }

    fun getUsers(): LiveData<Resource<List<User>>> =
        liveData(coroutineContext) {
            emit(Resource.Loading())

            when (val result = repository.getUsers()) {
                is Resource.Success -> {
                    if (result.data != null) {
                        emit(Resource.Success(data = result.data))
                    } else {
                        emit(
                            Resource.Error(
                                data = result.data,
                                message = "No Users found, it's null"
                            )
                        )
                    }
                }
                is Resource.Error -> {
                    emit(Resource.Error(message = result.message))
                }
                is Resource.Loading -> {
                    emit(Resource.Loading(data = result.data))
                }
            }
        }

    fun updateUser(user: User): LiveData<SimpleResource> =
        liveData(coroutineContext) {
            emit(Resource.Loading())

            when (val result = repository.updateUser(user)) {
                is Resource.Success -> {
                    emit(Resource.Success(Unit))
                }
                is Resource.Error -> {
                    emit(Resource.Error(message = result.message))
                }
                is Resource.Loading -> {
                    emit(Resource.Loading(Unit))
                }
            }
        }

    fun getFollowingActivities(user: User): LiveData<Resource<List<Event>>> =
        liveData(coroutineContext) {
            emit(Resource.Loading())

            when (val result = repository.getFollowingActivities(user)) {
                is Resource.Success -> {
                    if (result.data != null) {
                        emit(Resource.Success(data = result.data))
                    } else {
                        emit(
                            Resource.Error(
                                data = result.data,
                                message = "No activities found, it's null"
                            )
                        )
                    }
                }
                is Resource.Error -> {
                    emit(Resource.Error(message = result.message))
                }
                is Resource.Loading -> {
                    emit(Resource.Loading(data = result.data))
                }
            }
        }
}