package com.putrash.ocbcmobile.base

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.putrash.common.Event
import com.putrash.ocbcmobile.App

abstract class BaseViewModel : ViewModel() {

    protected val preferences: SharedPreferences get() = App.instance.preferences
    protected val authPreferences: SharedPreferences get() = App.instance.authPreferences

    private val _isLoading = MutableLiveData<Event<Boolean>>()
    val isLoading: LiveData<Event<Boolean>> get() = _isLoading

    protected fun showLoading() {
        _isLoading.postValue(Event(true))
    }

    protected fun hideLoading() {
        _isLoading.postValue(Event(false))
    }

    private val _errorMessage = MutableLiveData<Event<String>>()
    val errorMessage: LiveData<Event<String>> get() = _errorMessage

    protected fun showError(message: String) {
        _errorMessage.postValue(Event(message))
    }

    private val _signOut = MutableLiveData<Event<Boolean>>()
    val signOut: LiveData<Event<Boolean>> get() = _signOut

    protected fun setSignOut() {
        _signOut.postValue(Event(true))
    }
}