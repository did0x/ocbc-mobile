package com.putrash.ocbcmobile.arch.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.putrash.common.Event
import com.putrash.common.capitalize
import com.putrash.common.handleError
import com.putrash.data.Api
import com.putrash.data.model.User
import com.putrash.data.response.UserData
import com.putrash.ocbcmobile.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthViewModel(private val api: Api) : BaseViewModel() {

    private val _user = MutableLiveData<Event<UserData>>()
    val user: LiveData<Event<UserData>> get() = _user

    fun login(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showLoading()
                val response = api.login(user)
                when (response.code()) {
                    200 -> _user.postValue(Event(response.body()!!))
                    else -> {
                        val message = response.errorBody().handleError()
                        showError(message.capitalize())
                    }
                }
            } catch (throwable: Throwable) {
                throwable.printStackTrace()
                showError(throwable.message.toString().capitalize())
            } finally {
                hideLoading()
            }
        }
    }

    fun register(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showLoading()
                val response = api.register(user)
                when (response.code()) {
                    200 -> _user.postValue(Event(response.body()!!))
                    else -> {
                        val message = response.errorBody().handleError()
                        showError(message.capitalize())
                    }
                }
            } catch (throwable: Throwable) {
                throwable.printStackTrace()
                showError(throwable.message.toString().capitalize())
            } finally {
                hideLoading()
            }
        }
    }
}