package com.putrash.ocbcmobile.arch.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.putrash.common.Event
import com.putrash.common.capitalize
import com.putrash.common.handleError
import com.putrash.common.preferences.PreferencesHelper.get
import com.putrash.common.preferences.PreferencesHelper.remove
import com.putrash.common.preferences.PreferencesHelper.set
import com.putrash.common.preferences.PreferencesKey
import com.putrash.data.Api
import com.putrash.data.model.Payees
import com.putrash.data.model.Transfer
import com.putrash.data.response.BalanceData
import com.putrash.data.response.PayeesData
import com.putrash.data.response.TransactionData
import com.putrash.data.response.TransferData
import com.putrash.ocbcmobile.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class HomeViewModel(private val api: Api) : BaseViewModel() {

    private val _balance = MutableLiveData<Event<BalanceData>>()
    val balance: LiveData<Event<BalanceData>> get() = _balance

    private val _transaction = MutableLiveData<Event<TransactionData>>()
    val transaction: LiveData<Event<TransactionData>> get() = _transaction

    private val _transfer = MutableLiveData<Event<TransferData>>()
    val transfer: LiveData<Event<TransferData>> get() = _transfer

    private val _payees = MutableLiveData<Event<PayeesData>>()
    val payees: LiveData<Event<PayeesData>> get() = _payees

    private val _payeesHistory = MutableLiveData<Event<ArrayList<Payees>>>()
    val payeesHistory: LiveData<Event<ArrayList<Payees>>> get() = _payeesHistory

    private val _prompt = MutableLiveData<Event<Boolean>>()
    val prompt: LiveData<Event<Boolean>> get() = _prompt
    fun setPrompt(active: Boolean) = _prompt.postValue(Event(active))

    fun setLoading(active: Boolean) {
        if (active) showLoading()
        else hideLoading()
    }

    fun getBalance() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showLoading()
                val response = api.getBalance()
                when (response.code()) {
                    200 -> _balance.postValue(Event(response.body()!!))
                    401 -> setSignOut()
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

    fun getTransaction() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showLoading()
                val response = api.getTransactions()
                when (response.code()) {
                    200 -> _transaction.postValue(Event(response.body()!!))
                    401 -> setSignOut()
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

    fun getPayees() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showLoading()
                val response = api.getPayees()
                when (response.code()) {
                    200 -> _payees.postValue(Event(response.body()!!))
                    401 -> setSignOut()
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

    fun getPayeesHistory() {
        val data = preferences[PreferencesKey.PAYEES_HISTORY, arrayListOf<Payees>()]
        _payeesHistory.postValue(Event(data))
    }

    fun addPayeesHistory(accountNo: String, accountHolder: String) {
        val data = preferences[PreferencesKey.PAYEES_HISTORY, arrayListOf<Payees>()]
        val payees = Payees(accountNo = accountNo, name = accountHolder)
        if (payees !in data) data.add(payees)
        preferences.remove(PreferencesKey.PAYEES_HISTORY)
        preferences[PreferencesKey.PAYEES_HISTORY] = data
    }

    fun transfer(transfer: Transfer) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                showLoading()
                val response = api.transfer(transfer)
                when (response.code()) {
                    200 -> _transfer.postValue(Event(response.body()!!))
                    401 -> setSignOut()
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