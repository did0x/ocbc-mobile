package com.putrash.data

import com.putrash.data.model.Transfer
import com.putrash.data.model.User
import com.putrash.data.response.*
import retrofit2.Response
import retrofit2.http.*

interface Api {
    @POST("login")
    suspend fun login(@Body user: User) : Response<UserData>

    @POST("register")
    suspend fun register(@Body user: User) : Response<UserData>

    @POST("transfer")
    suspend fun transfer(@Body transfer: Transfer) : Response<TransferData>

    @GET("balance")
    suspend fun getBalance() : Response<BalanceData>

    @GET("payees")
    suspend fun getPayees() : Response<PayeesData>

    @GET("transactions")
    suspend fun getTransactions() : Response<TransactionData>

}