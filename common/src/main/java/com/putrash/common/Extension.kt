package com.putrash.common

import android.content.res.Resources
import android.os.SystemClock
import android.text.style.LocaleSpan
import android.view.View
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.putrash.data.response.ErrorData
import okhttp3.ResponseBody
import java.text.NumberFormat;
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

fun Int.toDp(): Int = (this / Resources.getSystem().displayMetrics.density).toInt()

fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

fun String.capitalize(): String {
    return this.replaceFirstChar { it.uppercase() }
}

fun String.parseCurrency(): Double {
    val currencyValue = this.replace(Regex("[SGD ,.]"), "")
    val result = currencyValue.ifEmpty { "0" }
    return (result.toDouble() / 100)
}

fun Double.toDollar(): String {
    val formatter = DecimalFormat("#,##0.00")
    return "SGD ${formatter.format((this))}"
}

fun Date.formatDate(): String {
    val formatter = SimpleDateFormat(Constants.DATE, Locale.getDefault())
    return formatter.format(this)
}

fun Date.formatTime(): String {
    val formatter = SimpleDateFormat(Constants.TIME, Locale.getDefault())
    return formatter.format(this)
}

fun String.parseDate(): Date {
    val formatter = SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault())
    return formatter.parse(this) ?: Date()
}

fun View.setSafeClickListener(throttle: Long = 600L, action: () -> Unit) {
    this.setOnClickListener(object : View.OnClickListener {
        private var lastClickTime: Long = 0

        override fun onClick(v: View) {
            if (SystemClock.elapsedRealtime() - lastClickTime < throttle) return
            else action()

            lastClickTime = SystemClock.elapsedRealtime()
        }
    })
}

inline fun <reified T> String.toObject(): T {
    return Gson().fromJson(this, object: TypeToken<T>() {}.type)
}

fun ResponseBody?.handleError(): String {
    val data = Gson().fromJson(this?.string(), ErrorData::class.java)
    return data.error
}