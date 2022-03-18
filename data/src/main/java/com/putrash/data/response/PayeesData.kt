package com.putrash.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class PayeesData(
    @SerializedName("status")
    val status: String = "",
    @SerializedName("data")
    val data: ArrayList<Payees>,
) : Parcelable {
    @Parcelize
    data class Payees(
        @SerializedName("id")
        val id: String = "",
        @SerializedName("name")
        val name: String = "",
        @SerializedName("accountNo")
        val accountNo: String = "",
    ) : Parcelable
}
