package com.putrash.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ErrorData(
    @SerializedName("status")
    val status: String = "",
    @SerializedName("error")
    val error: String = "",
) : Parcelable
