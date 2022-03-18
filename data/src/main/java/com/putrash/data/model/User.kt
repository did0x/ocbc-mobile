package com.putrash.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    @SerializedName("username")
    val username: String = "",
    @SerializedName("password")
    val password: String = ""
) : Parcelable
