package com.putrash.ocbcmobile.arch.ui.home

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.putrash.common.*
import com.putrash.common.Constants.BUNDLE_PAYEES
import com.putrash.common.Constants.BUNDLE_TRANSACTION
import com.putrash.common.preferences.PreferencesHelper.clear
import com.putrash.common.preferences.PreferencesHelper.get
import com.putrash.common.preferences.PreferencesKey
import com.putrash.data.model.Payees
import com.putrash.data.response.TransactionData
import com.putrash.ocbcmobile.R
import com.putrash.ocbcmobile.arch.adapter.PayeesHistoryAdapter
import com.putrash.ocbcmobile.arch.adapter.TransactionAdapter
import com.putrash.ocbcmobile.arch.vm.HomeViewModel
import com.putrash.ocbcmobile.base.BaseFragment
import com.putrash.ocbcmobile.databinding.FragmentHomeBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>(FragmentHomeBinding::inflate) {
    override val viewModel: HomeViewModel by sharedViewModel()
    private val transactionAdapter by lazy { TransactionAdapter(layoutInflater, ::onItemTransactionClick) }
    private val payeesAdapter by lazy { PayeesHistoryAdapter(layoutInflater, ::onItemPayeesClick) }

    private lateinit var transactionData: TransactionData
    private var username: String = ""
    private var accountNo: String = ""

    override fun initView(view: View, savedInstaceState: Bundle?) {
        initData()
        initClickListener()
        binding.rvSendAgain.adapter = payeesAdapter
        binding.rvHistoryToday.adapter = transactionAdapter
        binding.tvProfile.text = username[0].toString().capitalize()
        binding.tvName.text = username.capitalize()
        binding.tvAccountNo.text = accountNo
    }

    private fun initData() {
        username = preferences[PreferencesKey.USER_NAME, ""]
        accountNo = preferences[PreferencesKey.USER_ACCOUNT_NO, ""]
        viewModel.getBalance()
        viewModel.getTransaction()
        viewModel.getPayeesHistory()
    }

    override fun observeLiveData() {
        viewModel.balance.observe(viewLifecycleOwner, EventObserver { data ->
            binding.tvBalance.text = data.balance.toDollar()
        })
        viewModel.transaction.observe(viewLifecycleOwner, EventObserver {
            transactionData = it
            val data = handleDataToday(it.data)
            if (data.isNotEmpty() && it.data.isNotEmpty()) {
                binding.lblSeeAll.visibility = View.VISIBLE
                binding.rvHistoryToday.visibility = View.VISIBLE
                binding.contentHistoryEmpty.visibility = View.GONE
                transactionAdapter.submitList(data)
            } else if (data.isEmpty() && it.data.isNotEmpty()){
                binding.lblSeeAll.visibility = View.VISIBLE
                binding.rvHistoryToday.visibility = View.GONE
                binding.contentHistoryEmpty.visibility = View.VISIBLE
            } else {
                binding.lblSeeAll.visibility = View.GONE
                binding.rvHistoryToday.visibility = View.GONE
                binding.contentHistoryEmpty.visibility = View.VISIBLE
            }
        })
        viewModel.payeesHistory.observe(viewLifecycleOwner, EventObserver { data ->
            if (data.isNotEmpty()) {
                binding.contentSendAgain.visibility = View.VISIBLE
                binding.rvSendAgain.visibility = View.VISIBLE
                payeesAdapter.submitList(data)
            } else {
                binding.contentSendAgain.visibility = View.GONE
                binding.rvSendAgain.visibility = View.GONE
            }
        })
        super.observeLiveData()
    }

    private fun handleDataToday(data: ArrayList<TransactionData.Transaction>): ArrayList<TransactionData.Transaction> {
        val today = Date().formatDate()
        val transaction = data.filter {
            today == it.transactionDate.parseDate().formatDate()
        }
        return ArrayList(transaction)
    }

    private fun initClickListener() {
        binding.lblSeeAll.setSafeClickListener {
            val bundle = bundleOf(BUNDLE_TRANSACTION to transactionData)
            findNavController().navigate(R.id.action_homeFragment_to_transactionFragment, bundle)
        }
        binding.btnSend.setSafeClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_payFragment)
        }
        binding.btnScan.setSafeClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_scannerFragment)
        }
        binding.btnLogout.setSafeClickListener {
            preferences.clear()
            findNavController().navigate(R.id.loginFragment)
        }
    }

    private fun onItemPayeesClick(item: Payees) {
        val bundle = bundleOf(BUNDLE_PAYEES to item)
        findNavController().navigate(R.id.action_homeFragment_to_promptFragment, bundle)
    }

    private fun onItemTransactionClick(item: TransactionData.Transaction) = Unit
}