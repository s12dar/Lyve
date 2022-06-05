package com.tech.lyve.ui.fragments.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.tech.lyve.di.IoDispatcher
import com.tech.lyve.repositories.LyveRepository
import com.tech.lyve.utils.Resource
import com.tech.lyve.utils.SimpleResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: LyveRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val coroutineContext = viewModelScope.coroutineContext + ioDispatcher

    fun loginUser(email: String, pass: String): LiveData<SimpleResource> =
        liveData(coroutineContext) {
            emit(Resource.Loading())
            when (val result = repository.loginUser(email, pass)) {
                is Resource.Success -> {
                    emit(Resource.Success(data = Unit))
                }
                is Resource.Error -> {
                    emit(Resource.Error(message = result.message))
                }
                is Resource.Loading -> {
                    emit(Resource.Loading(data = Unit))
                }
            }
        }
}