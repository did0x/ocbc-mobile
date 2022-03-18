package com.putrash.ocbcmobile.arch.ui.home.transaction

import android.os.Bundle
import android.view.View
import com.putrash.common.Constants.BUNDLE_TRANSACTION
import com.putrash.common.formatDate
import com.putrash.common.parseDate
import com.putrash.data.model.Transaction
import com.putrash.data.response.TransactionData
import com.putrash.ocbcmobile.arch.adapter.TransactionFullAdapter
import com.putrash.ocbcmobile.arch.vm.HomeViewModel
import com.putrash.ocbcmobile.base.BaseDialogFragment
import com.putrash.ocbcmobile.databinding.FragmentTransactionBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class TransactionFragment : BaseDialogFragment<FragmentTransactionBinding, HomeViewModel>(FragmentTransactionBinding::inflate) {
    override val viewModel: HomeViewModel by viewModel()
    private val adapter by lazy { TransactionFullAdapter(layoutInflater, ::onItemClick) }
    private lateinit var transactionData: TransactionData

    override fun initView(view: View, savedInstaceState: Bundle?) {
        initBundle()
        adapter.submitList(handleData())
        binding.rvTransaction.adapter = adapter
    }

    private fun initBundle() {
        transactionData = (arguments?.getParcelable(BUNDLE_TRANSACTION) ?: TransactionData())
    }

    private fun handleData(): ArrayList<Transaction> {
        val dataArranged = arrayListOf<Transaction>()
        val dataWithDate = transactionData.data.groupBy { it.transactionDate.parseDate().formatDate() }
        for (data in dataWithDate) {
            dataArranged.add(
                Transaction(
                    transactionDate = data.key,
                    total = data.value.sumOf { it.amount },
                    type = 0
                )
            )
            data.value.forEach { transaction ->
                dataArranged.add(
                    Transaction(
                        transactionDate = data.key,
                        transaction = transaction,
                        total = data.value.sumOf { it.amount },
                        type = 1
                    )
                )
            }
        }
        return dataArranged
    }

    private fun onItemClick(item: Transaction) = Unit
}