package com.ebner.stundenplan.database.table.exam

import androidx.lifecycle.LiveData
import com.ebner.stundenplan.database.table.mergedEntities.ExamSubjectYearExamtype

/**
 * Created by raphael on 29.05.2020.
 * Stundenplan Created in com.ebner.stundenplan.database.table.exam
 */
class ExamRepository(
        private val examDao: ExamDao
) {
    /*---------------------Just pass the queries to the DAO--------------------------*/
    fun insert(exam: Exam) = examDao.insert(exam)
    fun update(exam: Exam) = examDao.update(exam)
    fun delete(exam: Exam) = examDao.delete(exam)

    //Live data view
    fun getAllExam(yid: Int): LiveData<List<ExamSubjectYearExamtype>> = examDao.getAllExam(yid)

}