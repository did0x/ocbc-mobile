package com.putrash.ocbcmobile.arch.adapter

import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import com.putrash.common.capitalize
import com.putrash.common.setSafeClickListener
import com.putrash.data.response.PayeesData
import com.putrash.ocbcmobile.R
import com.putrash.ocbcmobile.base.BaseListAdapter
import com.putrash.ocbcmobile.base.BaseViewHolder
import com.putrash.ocbcmobile.databinding.ItemUserBinding

class PayeesAdapter(
    layoutInflater: LayoutInflater,
    private val onClickListener: (PayeesData.Payees) -> Unit,
) : BaseListAdapter<PayeesData.Payees, ItemUserBinding, PayeesAdapter.PayeesViewHolder>(
    layoutInflater,
    ItemUserBinding::inflate,
    onClickListener,
    PayeesDiffCallback
) {

    private var selected: String = ""

    override fun itemViewHolder(
        viewBinding: ItemUserBinding,
        viewType: Int,
        onClickListener: (PayeesData.Payees) -> Unit,
    ) = PayeesViewHolder(viewBinding)

    override fun onBindViewHolder(holder: PayeesViewHolder, position: Int) = holder.onBind(getItem(position), position, onClickListener)

    inner class PayeesViewHolder(private val binding: ItemUserBinding) : BaseViewHolder<PayeesData.Payees>(binding.root) {
        override fun onBind(item: PayeesData.Payees, position: Int, onClickListener: (PayeesData.Payees) -> Unit) {
            binding.tvProfile.text = item.name[0].toString().capitalize()
            binding.tvProfile.isSelected = item.id == selected
            binding.tvProfile.setTextColor(
                if (item.id == selected) ContextCompat.getColor(binding.root.context, R.color.white)
                else ContextCompat.getColor(binding.root.context, R.color.red_500)
            )
            binding.tvName.text = item.name.capitalize()
            binding.root.setSafeClickListener {
                onClickListener(item)
                selected = item.id
                notifyItemRangeChanged(0, currentList.size)
            }
        }
    }

    object PayeesDiffCallback : DiffUtil.ItemCallback<PayeesData.Payees>() {
        override fun areItemsTheSame(oldItem: PayeesData.Payees, newItem: PayeesData.Payees) = oldItem == newItem
        override fun areContentsTheSame(oldItem: PayeesData.Payees, newItem: PayeesData.Payees) = oldItem.id == newItem.id
    }


}