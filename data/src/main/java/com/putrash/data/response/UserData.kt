package com.putrash.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserData(
    @SerializedName("status")
    val status: String = "",
    @SerializedName("token")
    val token: String = "",
    @SerializedName("username")
    val username: String = "",
    @SerializedName("accountNo")
    val accountNo: String = "",
) : Parcelable