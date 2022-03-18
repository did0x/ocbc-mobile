package com.putrash.ocbcmobile.base

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.putrash.common.Constants
import com.putrash.common.EventObserver
import com.putrash.common.component.ProgressDialog
import com.putrash.common.preferences.PreferencesHelper
import com.putrash.common.preferences.PreferencesHelper.clear
import com.putrash.common.toDp
import com.putrash.common.toPx
import com.putrash.ocbcmobile.R


abstract class BaseDialogFragment<VB: ViewBinding, VM: BaseViewModel>(
    private val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB,
) : BottomSheetDialogFragment() {

    private var _binding : VB? = null
    protected val binding get() = _binding!!

    protected val preferences: SharedPreferences by lazy {
        PreferencesHelper.init(requireContext(), resources.getString(R.string.app_name))
    }

    protected val authPreferences: SharedPreferences by lazy {
        PreferencesHelper.init(requireContext(), Constants.AUTH_PREF)
    }

    protected lateinit var progressDialog: ProgressDialog

    protected abstract val viewModel: VM
    protected abstract fun initView(view: View, savedInstaceState: Bundle?)

    open fun observeLiveData() {
        viewModel.errorMessage.observe(viewLifecycleOwner, EventObserver { message ->
            showSnackBar(message)
        })
        viewModel.isLoading.observe(viewLifecycleOwner, EventObserver { loading ->
            if (loading) {
                progressDialog.show()
            } else {
                progressDialog.dismiss()
            }
        })
        viewModel.signOut.observe(viewLifecycleOwner, EventObserver {
            preferences.clear()
            findNavController().navigate(R.id.loginFragment)
        })
    }

    fun showSnackBar(message: String) =
        Snackbar.make(dialog?.window?.decorView ?: binding.root, message, Snackbar.LENGTH_SHORT)
            .apply { show() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = bindingInflater.invoke(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressDialog = ProgressDialog(requireContext())
        initView(view, savedInstanceState)
        observeLiveData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}