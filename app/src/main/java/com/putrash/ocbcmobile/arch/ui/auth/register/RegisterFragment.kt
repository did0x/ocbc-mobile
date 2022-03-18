package com.putrash.ocbcmobile.arch.ui.auth.register

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.putrash.common.EventObserver
import com.putrash.common.preferences.PreferencesHelper.clear
import com.putrash.common.preferences.PreferencesHelper.set
import com.putrash.common.preferences.PreferencesKey
import com.putrash.common.setSafeClickListener
import com.putrash.data.model.User
import com.putrash.ocbcmobile.R
import com.putrash.ocbcmobile.arch.vm.AuthViewModel
import com.putrash.ocbcmobile.base.BaseFragment
import com.putrash.ocbcmobile.databinding.FragmentRegisterBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterFragment : BaseFragment<FragmentRegisterBinding, AuthViewModel>(FragmentRegisterBinding::inflate) {
    override val viewModel: AuthViewModel by viewModel()

    override fun initView(view: View, savedInstaceState: Bundle?) {
        initInputListener()
        initClickListener()
    }

    override fun observeLiveData() {
        super.observeLiveData()
        viewModel.user.observe(viewLifecycleOwner, EventObserver { data ->
            preferences[PreferencesKey.AUTH_KEY] = data.token
            preferences[PreferencesKey.USER_ACCOUNT_NO] = data.accountNo
            preferences[PreferencesKey.USER_NAME] = binding.etUsername.text.toString()
            findNavController().navigate(R.id.action_registerFragment_to_homeFragment)
        })
    }

    private fun register(username: String, password: String) {
        if (validate()) {
            authPreferences.clear()
            authPreferences[PreferencesKey.AUTH_USERNAME] = username
            authPreferences[PreferencesKey.AUTH_PASSWORD] = password
            viewModel.register(User(username = username, password = password))
        }
    }

    private fun initInputListener() {
        binding.etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
            override fun afterTextChanged(p0: Editable?) {
                validate(binding.etPassword)
            }

        })

        binding.etPasswordConfirm.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
            override fun afterTextChanged(p0: Editable?) {
                validate(binding.etPasswordConfirm)
            }

        })
    }

    private fun validate(editText: TextInputEditText? = null): Boolean {
        if (editText == binding.etPassword || editText == null) {
            if (binding.etPassword.text.toString().length < 6) {
                binding.tilPassword.error = getString(R.string.error_password_minimum)
            } else {
                binding.tilPassword.error = null
            }
        }

        if (editText == binding.etPasswordConfirm || editText == null) {
            if (!binding.etPasswordConfirm.text.toString().contentEquals(binding.etPassword.text.toString())) {
                binding.tilPasswordConfirm.error = getString(R.string.error_password_not_match)
            } else {
                binding.tilPasswordConfirm.error = null
            }
        }

        return binding.tilPassword.error == null && binding.tilPasswordConfirm.error == null
    }

    private fun initClickListener() {
        binding.btnRegister.setSafeClickListener {
            register(
                binding.etUsername.text.toString(),
                binding.etPassword.text.toString()
            )
        }
        binding.tvLogin.setSafeClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }
}