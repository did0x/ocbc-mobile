package com.putrash.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class TransactionData(
    @SerializedName("status")
    val status: String = "",
    @SerializedName("data")
    val data: ArrayList<Transaction> = arrayListOf(),
) : Parcelable {
    @Parcelize
    data class Transaction(
        @SerializedName("transactionId")
        val transactionId: String = "",
        @SerializedName("amount")
        val amount: Double = 0.0,
        @SerializedName("transactionDate")
        val transactionDate: String = "",
        @SerializedName("description")
        val description: String = "",
        @SerializedName("transactionType")
        val transactionType: String = "",
        @SerializedName("receipient")
        val receipient: Receipient = Receipient(),
        @SerializedName("sender")
        val sender: Sender = Sender(),
    ): Parcelable {
        @Parcelize
        data class Receipient(
            @SerializedName("accountNo")
            val accountNo: String = "",
            @SerializedName("accountHolder")
            val accountHolder: String = "",
        ) : Parcelable
        @Parcelize
        data class Sender(
            @SerializedName("accountNo")
            val accountNo: String = "",
            @SerializedName("accountHolder")
            val accountHolder: String = "",
        ): Parcelable
    }
}
