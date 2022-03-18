package com.putrash.ocbcmobile.arch.ui.auth.login

import android.content.Intent
import android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_STRONG
import android.hardware.biometrics.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.putrash.common.Constants.REQ_CODE_FINGERPRINT
import com.putrash.common.EventObserver
import com.putrash.common.preferences.PreferencesHelper.clear
import com.putrash.common.preferences.PreferencesHelper.get
import com.putrash.common.preferences.PreferencesHelper.set
import com.putrash.common.preferences.PreferencesKey
import com.putrash.common.setSafeClickListener
import com.putrash.data.model.User
import com.putrash.ocbcmobile.R
import com.putrash.ocbcmobile.arch.vm.AuthViewModel
import com.putrash.ocbcmobile.base.BaseFragment
import com.putrash.ocbcmobile.databinding.FragmentLoginBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : BaseFragment<FragmentLoginBinding, AuthViewModel>(FragmentLoginBinding::inflate) {
    override val viewModel: AuthViewModel by viewModel()
    private val promptInfo by lazy {
        BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.message_login_with_fingerprint))
            .setSubtitle(getString(R.string.message_fingerprint_guide))
            .setNegativeButtonText(getString(R.string.action_cancel))
            .build()
    }

    override fun initView(view: View, savedInstaceState: Bundle?) {
        initInputListener()
        initClickListener()
        checkBiometricSensor()
    }

    override fun observeLiveData() {
        super.observeLiveData()
        viewModel.user.observe(viewLifecycleOwner, EventObserver { data ->
            preferences[PreferencesKey.AUTH_KEY] = data.token
            preferences[PreferencesKey.USER_NAME] = data.username
            preferences[PreferencesKey.USER_ACCOUNT_NO] = data.accountNo
            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
        })
    }

    private fun fingerprintLogin() {
        val username = authPreferences[PreferencesKey.AUTH_USERNAME, ""]
        val password = authPreferences[PreferencesKey.AUTH_USERNAME, ""]
        if (username.isEmpty() || password.isEmpty()) {
            showSnackBar(getString(R.string.error_fingerprint_login_failed))
        } else {
            viewModel.login(User(username = username, password = password))
        }
    }

    private fun login(username: String, password: String) {
        if (validate()) {
            authPreferences.clear()
            authPreferences[PreferencesKey.AUTH_USERNAME] = username
            authPreferences[PreferencesKey.AUTH_PASSWORD] = password
            viewModel.login(User(username = username, password = password))
        }
    }

    private fun checkBiometricSensor() {
        binding.btnScan.visibility = when (authPreferences[PreferencesKey.HAS_BIOMETRIC, -1]) {
            BiometricManager.BIOMETRIC_SUCCESS,
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED
            -> View.VISIBLE
            else -> View.GONE
        }
    }

    private fun enrollFingerprint() {
        val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
            putExtra(
                Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                BIOMETRIC_STRONG or DEVICE_CREDENTIAL
            )
        }
        startActivityForResult(enrollIntent, REQ_CODE_FINGERPRINT)
    }

    private val biometricPrompt by lazy {
        BiometricPrompt(this, ContextCompat.getMainExecutor(context),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    if (errorCode !in listOf(10, 13)) {
                        val error = errString.toString().ifEmpty { getString(R.string.message_fingerprint_failed) }
                        showSnackBar(error)
                    }
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    fingerprintLogin()
                }
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQ_CODE_FINGERPRINT -> {
                val biometric = BiometricManager.from(requireContext()).canAuthenticate()
                authPreferences[PreferencesKey.HAS_BIOMETRIC] = biometric
                checkBiometricSensor()
            }
        }
    }

    private fun validate(editText: TextInputEditText? = null): Boolean {
        if (editText == binding.etPassword || editText == null) {
            if (binding.etPassword.text.toString().length < 6) {
                binding.tilPassword.error = getString(R.string.error_password_minimum)
            } else {
                binding.tilPassword.error = null
            }
        }

        return binding.tilPassword.error == null
    }

    private fun initInputListener() {
        binding.etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
            override fun afterTextChanged(p0: Editable?) {
                validate(binding.etPassword)
            }

        })
    }

    private fun initClickListener() {
        binding.btnLogin.setSafeClickListener {
            login(binding.etUsername.text.toString(), binding.etPassword.text.toString())
        }
        binding.btnScan.setSafeClickListener {
            val biometric = authPreferences[PreferencesKey.HAS_BIOMETRIC, -1]
            if (biometric != BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED) biometricPrompt.authenticate(promptInfo)
            else showSnackBar(getString(R.string.message_no_fingerprint)).apply {
                setAction(getString(R.string.action_tambah)) { enrollFingerprint() }
            }
        }
        binding.tvRegister.setSafeClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

}