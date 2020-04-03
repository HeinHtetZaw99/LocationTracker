package com.locationtracker.cache.dao


import androidx.room.*

@Dao
interface BaseDAO<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(t: T): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun bulkInsert(t: List<T>): Array<Long>

    @Delete
    fun delete(type: T)

    @Update
    fun update(type: T)
}