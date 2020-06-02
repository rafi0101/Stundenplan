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
    fun pendingExams(yid: Int): LiveData<List<ExamSubjectYearExamtype>> = examRepository.getPendingExams(yid)
    fun subjectExams(yid: Int, sid: Int): LiveData<List<ExamSubjectYearExamtype>> = examRepository.getSubjectExams(yid, sid)

    suspend fun subjectExamsSuspend(yid: Int, sid: Int): List<ExamSubjectYearExamtype> = examRepository.getSubjectExamsSuspend(yid, sid)

}