package com.appbase.components

import android.annotation.SuppressLint
import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Created By Daniel McCoy
 * This class is used for emitting single event .
 * As LiveData emits to all of its subscribers ,all the time till it was unsubscribed,
 * Such action is not necessary in the case of snackbars to show error message
 * This class is came into use in such applications where an action is needed to be handled only for once
 */
class SingleEventLiveData<T> : MediatorLiveData<T>() {
    private val mPending =
        AtomicBoolean(false)

    @SuppressLint("LogNotTimber")
    @MainThread
    override fun observe(
        owner: LifecycleOwner,
        observer: Observer<in T>
    ) {
        if (hasActiveObservers()) {
            Log.w(
                TAG,
                "Multiple observers registered but only one will be notified of changes."
            )
        }
        // Observe the internal MutableLiveData
        super.observe(owner, Observer { t: T ->
            if (mPending.compareAndSet(true, false)) {
                observer.onChanged(t)
            }
        })
    }

    @MainThread
    override fun setValue(t: T?) {
        mPending.set(true)
        super.setValue(t)
    }

    /**
     * Used for cases where T is Void, to make calls cleaner.
     */
    @MainThread
    fun call() {
        value = null
    }

    companion object {
        private const val TAG = "SingleLiveEvent"
    }
}