package com.ebner.stundenplan.database.table

import androidx.room.*

/**
 * Created by raphael on 16.05.2020.
 * Stundenplan Created in com.ebner.stundenplan.database.table
 */
//This contains the 3 default actions: instert, update, delete
@Dao
interface BaseDao<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg  obj: T)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(vararg  obj: T)

    @Delete
    fun delete(vararg  obj: T)
}