package com.putrash.data.model

import com.google.gson.annotations.SerializedName
import com.putrash.data.response.TransactionData
import java.io.Serializable

data class Transaction(
    @SerializedName("transactionDate")
    val transactionDate: String = "",
    @SerializedName("total")
    val total: Double = 0.0,
    @SerializedName("transaction")
    val transaction: TransactionData.Transaction = TransactionData.Transaction(),
    @SerializedName("type")
    val type: Int,
) : Serializable
