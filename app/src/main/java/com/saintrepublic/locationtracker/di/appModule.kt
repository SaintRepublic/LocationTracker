package com.saintrepublic.locationtracker.di

import com.saintrepublic.locationtracker.database.DatabaseService
import com.saintrepublic.locationtracker.vm.HistoryActivityViewModel
import com.saintrepublic.locationtracker.vm.MainActivityViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single { DatabaseService(androidContext()) }

    // ViewModels

    viewModel { MainActivityViewModel(androidContext(), get()) }
    viewModel { HistoryActivityViewModel(get()) }

}