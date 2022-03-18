package com.putrash.ocbcmobile.arch.adapter

import android.view.LayoutInflater
import androidx.recyclerview.widget.DiffUtil
import com.putrash.common.capitalize
import com.putrash.common.setSafeClickListener
import com.putrash.data.model.Payees
import com.putrash.ocbcmobile.base.BaseListAdapter
import com.putrash.ocbcmobile.base.BaseViewHolder
import com.putrash.ocbcmobile.databinding.ItemUserHistoryBinding

class PayeesHistoryAdapter(
    layoutInflater: LayoutInflater,
    private val onClickListener: (Payees) -> Unit,
) : BaseListAdapter<Payees, ItemUserHistoryBinding, PayeesHistoryAdapter.PayeesHistoryViewHolder>(
    layoutInflater,
    ItemUserHistoryBinding::inflate,
    onClickListener,
    PayeesDiffCallback
) {

    override fun itemViewHolder(
        viewBinding: ItemUserHistoryBinding,
        viewType: Int,
        onClickListener: (Payees) -> Unit,
    ) = PayeesHistoryViewHolder(viewBinding)

    override fun onBindViewHolder(holder: PayeesHistoryViewHolder, position: Int) = holder.onBind(getItem(position), position, onClickListener)

    inner class PayeesHistoryViewHolder(private val binding: ItemUserHistoryBinding) : BaseViewHolder<Payees>(binding.root) {
        override fun onBind(item: Payees, position: Int, onClickListener: (Payees) -> Unit) {
            binding.tvProfile.text = item.name[0].toString().capitalize()
            binding.tvName.text = item.name.capitalize()
            binding.root.setSafeClickListener {
                onClickListener(item)
            }
        }
    }

    object PayeesDiffCallback : DiffUtil.ItemCallback<Payees>() {
        override fun areItemsTheSame(oldItem: Payees, newItem: Payees) = oldItem == newItem
        override fun areContentsTheSame(oldItem: Payees, newItem: Payees) = oldItem.accountNo == newItem.accountNo
    }
}