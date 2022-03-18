package com.putrash.ocbcmobile.di

import com.putrash.ocbcmobile.arch.vm.HomeViewModel
import com.putrash.ocbcmobile.arch.vm.AuthViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { AuthViewModel(get()) }
    viewModel { HomeViewModel(get()) }
}