package com.ebner.stundenplan.database.table.exam

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.ebner.stundenplan.database.table.BaseDao
import com.ebner.stundenplan.database.table.mergedEntities.ExamSubjectYearExamtype

/**
 * Created by raphael on 29.05.2020.
 * Stundenplan Created in com.ebner.stundenplan.database.table.exam
 */
@Dao
interface ExamDao : BaseDao<Exam> {

    @Transaction
    @Query("SELECT * FROM exam INNER JOIN subject ON subject.sid = exam.e_sid INNER JOIN year ON year.yid = exam.e_yid INNER JOIN examtype ON examtype.etid = exam.e_etid WHERE e_yid=:yid ORDER BY edateyear ASC, edatemonth ASC, edateday ASC")
    fun getAllExam(yid: Int): LiveData<List<ExamSubjectYearExamtype>>

    @Transaction
    @Query("SELECT * FROM exam INNER JOIN subject ON subject.sid = exam.e_sid INNER JOIN year ON year.yid = exam.e_yid INNER JOIN examtype ON examtype.etid = exam.e_etid WHERE e_yid=:yid AND egrade = -1 ORDER BY edateyear ASC, edatemonth ASC, edateday ASC")
    fun getPendingExams(yid: Int): LiveData<List<ExamSubjectYearExamtype>>

    @Transaction
    @Query("SELECT * FROM exam INNER JOIN subject ON subject.sid = exam.e_sid INNER JOIN year ON year.yid = exam.e_yid INNER JOIN examtype ON examtype.etid = exam.e_etid WHERE e_yid=:yid AND e_sid=:sid ORDER BY edateyear ASC, edatemonth ASC, edateday ASC")
    fun getSubjectExams(yid: Int, sid: Int): LiveData<List<ExamSubjectYearExamtype>>

}