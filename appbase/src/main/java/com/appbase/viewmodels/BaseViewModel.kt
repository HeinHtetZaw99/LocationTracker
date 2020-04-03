package com.pv.viewmodels

import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


abstract class BaseViewModel : ViewModel() {
    protected val compositeDisposable: CompositeDisposable = CompositeDisposable()
    protected val PROVIDER_FACEBOOK = "FACEBOOK"
    protected val PROVIDER_CREDENTIALS = "CREDENTAILS"

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

    fun <T> Observable<T>.handle(): Observable<T> {
        return this.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

}
