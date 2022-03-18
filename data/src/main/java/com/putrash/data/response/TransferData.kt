package com.putrash.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class TransferData(
    @SerializedName("status")
    val status: String = "",
    @SerializedName("transactionId")
    val transactionId: String = "",
    @SerializedName("amount")
    val amount: Double = 0.0,
    @SerializedName("description")
    val description: String = "",
    @SerializedName("recipientAccount")
    val recipientAccount: String = "",
) : Parcelable
