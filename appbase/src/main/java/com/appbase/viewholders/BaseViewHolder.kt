package com.appbase.viewholders


import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.view.View

import androidx.recyclerview.widget.RecyclerView


abstract class BaseViewHolder<W>(itemView: View) : RecyclerView.ViewHolder(itemView){


    fun getScreenWidth(context: Context, percentage: Double): Int {
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        return (displayMetrics.widthPixels * percentage).toInt()
    }

//    abstract fun onSelected(index : Int )

    abstract fun setData(data: W)
}
