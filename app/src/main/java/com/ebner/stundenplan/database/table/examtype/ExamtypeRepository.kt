package com.ebner.stundenplan.database.table.examtype

import androidx.lifecycle.LiveData

/**
 * Created by raphael on 25.05.2020.
 * Stundenplan Created in com.ebner.stundenplan.database.table.examtype
 */
class ExamtypeRepository(
        private val examtypeDao: ExamtypeDao
) {
    /*---------------------Just pass the queries to the DAO--------------------------*/
    fun insert(examtype: Examtype) = examtypeDao.insert(examtype)
    fun update(examtype: Examtype) = examtypeDao.update(examtype)
    fun delete(examtype: Examtype) = examtypeDao.delete(examtype)

    //Live data view
    val getAllExamtype: LiveData<List<Examtype>> = examtypeDao.getAllExamtype()

    suspend fun getAllExamtypeList(): List<Examtype> = examtypeDao.getAllExamtypeList()
}