package com.putrash.ocbcmobile.arch.adapter

import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import com.putrash.common.*
import com.putrash.data.model.Transaction
import com.putrash.ocbcmobile.R
import com.putrash.ocbcmobile.base.BaseListAdapter
import com.putrash.ocbcmobile.base.BaseViewHolder
import com.putrash.ocbcmobile.databinding.ItemTransactionFullBinding

class TransactionFullAdapter(
    layoutInflater: LayoutInflater,
    private val onClickListener: (Transaction) -> Unit,
) : BaseListAdapter<Transaction, ItemTransactionFullBinding, TransactionFullAdapter.TransactionViewHolder>(
    layoutInflater,
    ItemTransactionFullBinding::inflate,
    onClickListener,
    TransactionDiffCallback
) {

    override fun itemViewHolder(
        viewBinding: ItemTransactionFullBinding,
        viewType: Int,
        onClickListener: (Transaction) -> Unit,
    ) = TransactionViewHolder(viewBinding)

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) = holder.onBind(getItem(position), position, onClickListener)

    inner class TransactionViewHolder(private val binding: ItemTransactionFullBinding) :
        BaseViewHolder<Transaction>(binding.root) {
        override fun onBind(item: Transaction, position: Int, onClickListener: (Transaction) -> Unit) {
            if (item.type == 0) {
                binding.contentHeader.visibility = View.VISIBLE
                binding.contentBody.visibility = View.GONE
                binding.tvToday.text = item.transactionDate
                binding.tvAmount.text = item.total.toDollar()
            }
            if (item.type == 1) {
                binding.contentHeader.visibility = View.GONE
                binding.contentBody.visibility = View.VISIBLE
                binding.tvTime.text = item.transaction.transactionDate.parseDate().formatTime()
                binding.tvType.text = item.transaction.transactionType.capitalize()
                if (item.transaction.transactionType == "received") {
                    binding.tvProfile.text = item.transaction.sender.accountHolder[0].toString().capitalize()
                    binding.tvName.text = item.transaction.sender.accountHolder.capitalize()
                    binding.tvAmount.text = "+ ${item.transaction.amount.toDollar()}"
                    binding.tvAmount.setTextColor(ContextCompat.getColor(binding.root.context, R.color.green_500))
                }
                if (item.transaction.transactionType == "transfer") {
                    binding.tvProfile.text = item.transaction.receipient.accountHolder[0].toString().capitalize()
                    binding.tvName.text = item.transaction.receipient.accountHolder.capitalize()
                    binding.tvAmount.text = "- ${item.transaction.amount.toDollar()}"
                    binding.tvAmount.setTextColor(ContextCompat.getColor(binding.root.context, R.color.red_700))
                }

            }
        }
    }

    object TransactionDiffCallback : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction) = oldItem == newItem
        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction) =
            oldItem.transactionDate == newItem.transactionDate
    }
}