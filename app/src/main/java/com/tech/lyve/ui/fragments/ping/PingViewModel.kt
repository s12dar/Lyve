package com.tech.lyve.ui.fragments.ping

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.tech.lyve.di.IoDispatcher
import com.tech.lyve.models.Event
import com.tech.lyve.models.User
import com.tech.lyve.repositories.LyveRepository
import com.tech.lyve.utils.Resource
import com.tech.lyve.utils.SimpleResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@HiltViewModel
class PingViewModel @Inject constructor(
    private val repository: LyveRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val coroutineContext = viewModelScope.coroutineContext + ioDispatcher

    fun getEventBelongingToCurrentUser(user: User): LiveData<Resource<List<Event>>> =
        liveData(coroutineContext) {
            emit(Resource.Loading())

            when (val result = repository.getEventBelongingToCurrentUser(user)) {
                is Resource.Success -> {
                    if (result.data != null) {
                        emit(Resource.Success(data = result.data))
                    } else {
                        emit(
                            Resource.Error(
                                data = result.data,
                                message = "No events found, it's null"
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

    fun updateEvent(event: Event, user: User): LiveData<SimpleResource> =
        liveData(coroutineContext) {
            emit(Resource.Loading())

            when (val result = repository.updateActivity(event, user)) {
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
}