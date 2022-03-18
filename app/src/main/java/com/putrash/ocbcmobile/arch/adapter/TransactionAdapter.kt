package com.putrash.ocbcmobile.arch.adapter

import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import com.putrash.common.capitalize
import com.putrash.common.formatTime
import com.putrash.common.parseDate
import com.putrash.common.toDollar
import com.putrash.data.response.TransactionData
import com.putrash.ocbcmobile.R
import com.putrash.ocbcmobile.base.BaseListAdapter
import com.putrash.ocbcmobile.base.BaseViewHolder
import com.putrash.ocbcmobile.databinding.ItemTransactionBinding

class TransactionAdapter(
    layoutInflater: LayoutInflater,
    private val onClickListener: (TransactionData.Transaction) -> Unit
    ) : BaseListAdapter<TransactionData.Transaction, ItemTransactionBinding, TransactionAdapter.TransactionViewHolder>(
    layoutInflater,
    ItemTransactionBinding::inflate,
    onClickListener,
    TransactionDiffCallback
    ) {

        override fun itemViewHolder(
            viewBinding: ItemTransactionBinding,
            viewType: Int,
            onClickListener: (TransactionData.Transaction) -> Unit,
        ) = TransactionViewHolder(viewBinding)

        override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) = holder.onBind(getItem(position), position, onClickListener)

        inner class TransactionViewHolder(private val binding: ItemTransactionBinding) : BaseViewHolder<TransactionData.Transaction>(binding.root) {
            override fun onBind(item: TransactionData.Transaction, position: Int, onClickListener: (TransactionData.Transaction) -> Unit) {
                if (item.transactionType == "received") {
                    binding.tvProfile.text = item.sender.accountHolder[0].toString().capitalize()
                    binding.tvName.text = item.sender.accountHolder.capitalize()
                    binding.tvAmount.text = "+ ${item.amount.toDollar()}"
                    binding.tvAmount.setTextColor(ContextCompat.getColor(binding.root.context, R.color.green_500))
                }
                if (item.transactionType == "transfer") {
                    binding.tvProfile.text = item.receipient.accountHolder[0].toString().capitalize()
                    binding.tvName.text = item.receipient.accountHolder.capitalize()
                    binding.tvAmount.text = "- ${item.amount.toDollar()}"
                    binding.tvAmount.setTextColor(ContextCompat.getColor(binding.root.context, R.color.red_700))

                }
                binding.tvTime.text = item.transactionDate.parseDate().formatTime()
                binding.tvType.text = item.transactionType.capitalize()
            }
        }

        object TransactionDiffCallback : DiffUtil.ItemCallback<TransactionData.Transaction>() {
            override fun areItemsTheSame(oldItem: TransactionData.Transaction, newItem: TransactionData.Transaction) = oldItem == newItem
            override fun areContentsTheSame(oldItem: TransactionData.Transaction, newItem: TransactionData.Transaction) = oldItem.transactionId == newItem.transactionId
        }
    }