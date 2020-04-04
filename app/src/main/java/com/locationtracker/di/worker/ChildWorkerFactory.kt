package com.locationtracker.di.worker

import androidx.work.WorkerParameters
import android.content.Context
import androidx.work.ListenableWorker

interface ChildWorkerFactory {
    fun create(context: Context, params: WorkerParameters): ListenableWorker
}