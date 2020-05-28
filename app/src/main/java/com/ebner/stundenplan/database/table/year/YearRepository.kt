package com.ebner.stundenplan.database.table.year

import androidx.lifecycle.LiveData

/**
 * Created by raphael on 25.05.2020.
 * Stundenplan Created in com.ebner.stundenplan.database.table.year
 */
class YearRepository(
        private val yearDao: YearDao
) {
    /*---------------------Just pass the queries to the DAO--------------------------*/
    fun insert(year: Year) = yearDao.insert(year)
    fun update(year: Year) = yearDao.update(year)
    fun delete(year: Year) = yearDao.delete(year)

    //Live data view
    val getAllYear: LiveData<List<Year>> = yearDao.getAllYear()

    suspend fun getAllYearList(): List<Year> = yearDao.getAllYearList()


}