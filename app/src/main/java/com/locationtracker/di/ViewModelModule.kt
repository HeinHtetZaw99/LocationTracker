package com.locationtracker.di

import androidx.lifecycle.ViewModel
import com.appbase.di.viewModel.ViewModelKey
import com.locationtracker.MainViewModel

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun mainViewModel(
        mainViewModel: MainViewModel
    ): ViewModel

}
