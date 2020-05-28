package com.ebner.stundenplan.database.table.year

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.ebner.stundenplan.database.table.BaseDao

/**
 * Created by raphael on 25.05.2020.
 * Stundenplan Created in com.ebner.stundenplan.database.table.year
 */
@Dao
interface YearDao : BaseDao<Year> {

    @Query("SELECT * FROM Year ORDER BY yname ASC")
    fun getAllYear(): LiveData<List<Year>>


    @Query("SELECT * FROM Year ORDER BY yname ASC")
    suspend fun getAllYearList(): List<Year>
}