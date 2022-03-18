package com.putrash.ocbcmobile.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseListAdapter<T, VB: ViewBinding, VH:RecyclerView.ViewHolder>(
    private val layoutInflater: LayoutInflater,
    private val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB,
    private val onClickListener: (T) -> Unit,
    diffCallback: DiffUtil.ItemCallback<T>
) : ListAdapter<T, VH>(diffCallback) {

    protected abstract fun itemViewHolder(
        viewBinding: VB,
        viewType: Int,
        onClickListener: (T) -> Unit
    ) : VH

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val viewBinding = bindingInflater.invoke(layoutInflater, parent, false)
        return itemViewHolder(viewBinding, viewType, onClickListener)
    }
}