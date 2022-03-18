package com.putrash.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Transfer(
    @SerializedName("receipientAccountNo")
    val receipientAccountNo: String = "",
    @SerializedName("amount")
    val amount: Double = 0.0,
    @SerializedName("description")
    val description: String = "",
) : Parcelable
