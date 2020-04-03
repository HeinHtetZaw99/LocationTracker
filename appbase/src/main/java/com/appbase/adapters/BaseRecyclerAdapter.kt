package com.appbase.adapters


import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.appbase.components.DiffUtils
import com.appbase.viewholders.BaseViewHolder
import java.util.*

/**Created by Daniel McCoy @ 25th Feb 2020*/
abstract class BaseRecyclerAdapter<itemType, viewType : BaseViewHolder<itemType>> :
    RecyclerView.Adapter<viewType>() {

    protected var dataList: MutableList<itemType>? = null
    protected var updatedIndex: Int = -1


    val items: List<itemType>
        get() = if (dataList == null) ArrayList() else dataList!!

    init {
        dataList = ArrayList()

    }


    override fun onBindViewHolder(holder: viewType, position: Int) {
        if (dataList!!.isNotEmpty()) {
            holder.setData(dataList!![position])
        }
    }

    override fun getItemCount(): Int {
        return dataList!!.size
    }

    fun getItemAt(position: Int): itemType? {
        return if (position < dataList!!.size - 1) dataList!![position] else null

    }

    fun addNewData(newItem: itemType, position: Int) {
        if (dataList != null) {
            dataList!!.add(position, newItem)
            notifyDataSetChanged()
        }
    }

    open fun update(newDataList: List<itemType>) {
        val diffResult = DiffUtil.calculateDiff(DiffUtils(newDataList, dataList!!))
        diffResult.dispatchUpdatesTo(this)
        notifyDataSetChanged()
    }


    open fun appendDataAndUpdate(newData: List<itemType>) {
        clearData()
        if (dataList!!.isEmpty()) {
            dataList!!.addAll(newData)
            notifyDataSetChanged()
        } else
            update(newData)

    }

    fun appendNewData(newData: List<itemType>) {
        clearData()
        dataList!!.addAll(newData)
        notifyDataSetChanged()
    }

    fun removeData(data: itemType) {
        this.dataList!!.remove(data)
        notifyDataSetChanged()
    }

    open fun addNewData(data: itemType) {
        this.dataList!!.add(data)
        notifyDataSetChanged()
        notifyItemInserted(dataList!!.lastIndex)
    }

    fun addNewDataList(dataList: List<itemType>) {
        this.dataList!!.addAll(dataList)
        notifyDataSetChanged()
    }

    fun clearData() {
        dataList = ArrayList()
        notifyDataSetChanged()
    }


    fun updateItemAt(index: Int, item: itemType) {
        dataList!![index] = item
        notifyItemChanged(index)
    }

}
