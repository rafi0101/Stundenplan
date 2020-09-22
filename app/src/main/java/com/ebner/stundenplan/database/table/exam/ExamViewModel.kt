package com.ebner.stundenplan.database.table.exam

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.ebner.stundenplan.database.main.StundenplanDatabase
import com.ebner.stundenplan.database.table.mergedEntities.ExamSubjectYearExamtype
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by raphael on 29.05.2020.
 * Stundenplan Created in com.ebner.stundenplan.database.table.exam
 */
class ExamViewModel(application: Application) : AndroidViewModel(application) {

    private val examRepository: ExamRepository


    /*---------------------Define the Database, and the Repository--------------------------*/
    init {
        val examDao = StundenplanDatabase.getInstance(application).examDao()
        examRepository = ExamRepository(examDao)


    }

    /*---------------------Define default queries--------------------------*/
    fun insert(exam: Exam) = viewModelScope.launch(Dispatchers.IO) {
        examRepository.insert(exam)
    }

    fun update(exam: Exam) = viewModelScope.launch(Dispatchers.IO) {
        examRepository.update(exam)
    }

    fun delete(exam: Exam) = viewModelScope.launch(Dispatchers.IO) {
        examRepository.delete(exam)
    }

    fun allExam(yid: Int): LiveData<List<ExamSubjectYearExamtype>> = examRepository.getAllExam(yid)
    fun allExamPending(yid: Int): LiveData<List<ExamSubjectYearExamtype>> = examRepository.getAllExamPending(yid)
    fun allExamBySubject(yid: Int, sid: Int): LiveData<List<ExamSubjectYearExamtype>> = examRepository.getAllExamBySubject(yid, sid)
    fun allExamPendingBySubject(yid: Int, sid: Int): LiveData<List<ExamSubjectYearExamtype>> = examRepository.getAllExamPendingBySubject(yid, sid)
    fun allExamByOrder(yid: Int, order: Int): LiveData<List<ExamSubjectYearExamtype>> = examRepository.getAllExamByOrder(yid, order)
    fun allExamBySubjectOrder(yid: Int, sid: Int, order: Int): LiveData<List<ExamSubjectYearExamtype>> = examRepository.getAllExamBySubjectOrder(yid, sid, order)


    fun pendingExams(yid: Int): LiveData<List<ExamSubjectYearExamtype>> = examRepository.getPendingExams(yid)
    fun subjectExams(yid: Int, sid: Int): LiveData<List<ExamSubjectYearExamtype>> = examRepository.getSubjectExams(yid, sid)

    suspend fun subjectExamsSuspend(yid: Int, sid: Int): List<ExamSubjectYearExamtype> = examRepository.getSubjectExamsSuspend(yid, sid)
    suspend fun allExamSuspend(yid: Int): List<ExamSubjectYearExamtype> = examRepository.getAllExamSuspend(yid)

}