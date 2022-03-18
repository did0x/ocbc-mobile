package com.putrash.ocbcmobile.arch.ui.home.prompt

import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.putrash.common.*
import com.putrash.common.Constants.BUNDLE_PAYEES
import com.putrash.data.model.Payees
import com.putrash.data.model.Transfer
import com.putrash.ocbcmobile.R
import com.putrash.ocbcmobile.arch.vm.HomeViewModel
import com.putrash.ocbcmobile.base.BaseDialogFragment
import com.putrash.ocbcmobile.databinding.FragmentPromptBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class PromptFragment : BaseDialogFragment<FragmentPromptBinding, HomeViewModel>(FragmentPromptBinding::inflate) {
    override val viewModel: HomeViewModel by sharedViewModel()

    private var accountNo = ""
    private var accountHolder = ""
    private var amount = 0.0

    override fun initView(view: View, savedInstaceState: Bundle?) {
        initBundle()
        initInputListener()
        binding.tvProfile.text = accountHolder[0].toString().capitalize()
        binding.tvName.text = accountHolder
        binding.tvAccountNo.text = accountNo
        binding.btnTransfer.setSafeClickListener {
            transfer(accountNo, amount)
        }
    }

    private fun transfer(accountNo: String, amount: Double) {
        if (validate()) {
            viewModel.transfer(Transfer(receipientAccountNo = accountNo, amount = amount))
        }
    }

    private fun initBundle() {
        val data = (arguments?.getParcelable(BUNDLE_PAYEES) ?: Payees())
        accountNo = data.accountNo
        accountHolder = data.name
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

    override fun observeLiveData() {
        super.observeLiveData()
        viewModel.transfer.observe(viewLifecycleOwner, EventObserver {
            viewModel.addPayeesHistory(accountNo, accountHolder)
            when (findNavController().previousBackStackEntry?.destination?.id) {
                R.id.scannerFragment -> {
                    findNavController().navigate(R.id.action_promptFragment_to_homeFragment)
                }
                else -> {
                    viewModel.getBalance()
                    viewModel.getTransaction()
                    viewModel.getPayeesHistory()
                    dismiss()
                }
            }
        })
    }

    override fun onDismiss(dialog: DialogInterface) {
        when (findNavController().previousBackStackEntry?.destination?.id) {
            R.id.scannerFragment -> viewModel.setPrompt(false)
        }
        super.onDismiss(dialog)
    }
}