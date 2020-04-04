package com.locationtracker.di

import androidx.lifecycle.ViewModel
import com.appbase.di.viewModel.ViewModelKey
import com.locationtracker.viewmodels.MainViewModel

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    //todo manual : In case of declaring one new viewmodel  please declare that viewmodel as the following methods
    /**
    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun mainViewModel(
    mainViewModel: MainViewModel
    ): ViewModel

    Method name doesn't matter,  the  @ViewModelKey must be the viewmodel u have declared and must return "ViewModel"
     */
    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun mainViewModel(
        mainViewModel: MainViewModel
    ): ViewModel

}
