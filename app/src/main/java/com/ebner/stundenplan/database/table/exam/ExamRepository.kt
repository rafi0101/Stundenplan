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
    fun getAllExamPending(yid: Int): LiveData<List<ExamSubjectYearExamtype>> = examDao.getAllExamPending(yid)
    fun getAllExamBySubject(yid: Int, sid: Int): LiveData<List<ExamSubjectYearExamtype>> = examDao.getAllExamBySubject(yid, sid)
    fun getAllExamPendingBySubject(yid: Int, sid: Int): LiveData<List<ExamSubjectYearExamtype>> = examDao.getAllExamPendingBySubject(yid, sid)
    fun getAllExamByOrder(yid: Int, order: Int): LiveData<List<ExamSubjectYearExamtype>> =
            when (order) {
                //1 = Fach, 2 = Prüfungs Art, (3 = Note)
                1 -> examDao.getAllExamByOrderSubject(yid)
                2 -> examDao.getAllExamByOrderExamtype(yid)
                3 -> examDao.getAllExamByOrderGrade(yid)
                else -> examDao.getAllExam(yid)
            }

    fun getAllExamBySubjectOrder(yid: Int, sid: Int, order: Int): LiveData<List<ExamSubjectYearExamtype>> =
            when (order) {
                //1 = Fach, 2 = Prüfungs Art, (3 = Note)
                1 -> examDao.getAllExamBySubjectOrderSubject(yid, sid)
                2 -> examDao.getAllExamBySubjectOrderExamtype(yid, sid)
                3 -> examDao.getAllExamBySubjectOrderGrade(yid, sid)
                else -> examDao.getAllExamBySubject(yid, sid)
            }

    fun getSubjectExams(yid: Int, sid: Int): LiveData<List<ExamSubjectYearExamtype>> = examDao.getSubjectExams(yid, sid)

    suspend fun getSubjectExamsSuspend(yid: Int, sid: Int): List<ExamSubjectYearExamtype> = examDao.getSubjectExamsSuspend(yid, sid)
    fun getAllExamSuspend(yid: Int): List<ExamSubjectYearExamtype> = examDao.getAllExamSuspend(yid)
}