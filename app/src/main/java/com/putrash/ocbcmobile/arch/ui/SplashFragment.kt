package com.putrash.ocbcmobile.arch.ui

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.putrash.common.preferences.PreferencesHelper.get
import com.putrash.common.preferences.PreferencesKey.AUTH_KEY
import com.putrash.ocbcmobile.R
import com.putrash.ocbcmobile.arch.vm.EmptyViewModel
import com.putrash.ocbcmobile.base.BaseFragment
import com.putrash.ocbcmobile.databinding.FragmentSplashBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SplashFragment : BaseFragment<FragmentSplashBinding, EmptyViewModel>(FragmentSplashBinding::inflate) {

    override fun initView(view: View, savedInstaceState: Bundle?) {
        lifecycleScope.launch {
            delay(1500)
            findNavController().navigate(
                if (preferences[AUTH_KEY, ""] == "") R.id.action_splashFragment_to_loginFragment
                else R.id.action_splashFragment_to_homeFragment
            )
        }
    }

    override fun observeLiveData() = Unit
    override val viewModel: EmptyViewModel by viewModel()
}