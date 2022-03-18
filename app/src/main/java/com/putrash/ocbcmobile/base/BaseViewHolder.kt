package com.putrash.ocbcmobile.base

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun onBind(item: T, position:Int, onClickListener: (T) -> Unit)
}