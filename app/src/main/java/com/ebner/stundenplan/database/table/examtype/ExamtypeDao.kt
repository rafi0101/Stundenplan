package com.ebner.stundenplan.database.table.examtype

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.ebner.stundenplan.database.table.BaseDao

/**
 * Created by raphael on 25.05.2020.
 * Stundenplan Created in com.ebner.stundenplan.database.table.examtype
 */
@Dao
interface ExamtypeDao : BaseDao<Examtype> {

    @Query("SELECT * FROM examtype ORDER BY etname ASC")
    fun getAllExamtype(): LiveData<List<Examtype>>


    @Query("SELECT * FROM examtype ORDER BY etname ASC")
    suspend fun getAllExamtypeList(): List<Examtype>

    @Query("SELECT * FROM examtype WHERE etid=:etid")
    suspend fun getExamtypeByID(etid: Int): Examtype

}