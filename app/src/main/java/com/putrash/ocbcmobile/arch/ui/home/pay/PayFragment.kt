package com.putrash.ocbcmobile.arch.ui.home.pay

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.google.android.material.textfield.TextInputEditText
import com.putrash.common.EventObserver
import com.putrash.common.parseCurrency
import com.putrash.common.setSafeClickListener
import com.putrash.common.toDollar
import com.putrash.data.model.Transfer
import com.putrash.data.response.PayeesData
import com.putrash.ocbcmobile.R
import com.putrash.ocbcmobile.arch.adapter.PayeesAdapter
import com.putrash.ocbcmobile.arch.vm.HomeViewModel
import com.putrash.ocbcmobile.base.BaseDialogFragment
import com.putrash.ocbcmobile.databinding.FragmentPayBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class PayFragment : BaseDialogFragment<FragmentPayBinding, HomeViewModel>(FragmentPayBinding::inflate) {
    override val viewModel: HomeViewModel by sharedViewModel()
    private val adapter by lazy { PayeesAdapter(layoutInflater, ::onItemClick) }

    private var accountNo = ""
    private var accountHolder = ""
    private var amount = 0.0

    override fun initView(view: View, savedInstaceState: Bundle?) {
        initData()
        initInputListener()
        binding.rvPayess.adapter = adapter
        binding.btnTransfer.setSafeClickListener {
            transfer(accountNo, amount)
        }
    }

    private fun initData() {
        viewModel.getPayees()
    }

    override fun observeLiveData() {
        super.observeLiveData()
        viewModel.payees.observe(viewLifecycleOwner, EventObserver {
            adapter.submitList(it.data)
        })
        viewModel.transfer.observe(viewLifecycleOwner, EventObserver {
            viewModel.addPayeesHistory(accountNo, accountHolder)
            viewModel.getBalance()
            viewModel.getTransaction()
            viewModel.getPayeesHistory()
            dismiss()
        })
    }

    private fun transfer(accountNo: String, amount: Double) {
        if (validate()) {
            viewModel.transfer(Transfer(receipientAccountNo = accountNo, amount = amount))
        }
    }

    private fun validate(editText: TextInputEditText? = null): Boolean {
        if (editText == binding.etAmount || editText == null) {
            if (amount == 0.00) {
                binding.tilAmount.error = getString(R.string.error_amount_must_filled)
            } else {
                binding.tilAmount.error = null
            }
        }

        return binding.tilAmount.error == null
    }

    private fun initInputListener() {
        binding.etAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
            override fun afterTextChanged(p0: Editable?) {
                binding.etAmount.removeTextChangedListener(this)
                val value = (p0 ?: "").toString()

                val valueParsed = value.parseCurrency()
                val valueFormatted = valueParsed.toDollar()
                amount = valueParsed
                binding.etAmount.setText(valueFormatted)
                binding.etAmount.setSelection(valueFormatted.length)
                binding.etAmount.addTextChangedListener(this)
                validate(binding.etAmount)
            }

        })
    }

    private fun onItemClick(item: PayeesData.Payees) {
        accountNo = item.accountNo
        accountHolder = item.name
    }
}