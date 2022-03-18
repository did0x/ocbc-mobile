package com.putrash.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class BalanceData(
    @SerializedName("status")
    val status: String = "",
    @SerializedName("accountNo")
    val accountNo: String = "",
    @SerializedName("balance")
    val balance: Double = 0.0,
) : Parcelable
