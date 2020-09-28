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
    @Query("SELECT * FROM exam INNER JOIN subject ON subject.sid = exam.e_sid INNER JOIN year ON year.yid = exam.e_yid INNER JOIN examtype ON examtype.etid = exam.e_etid WHERE e_yid=:yid  ORDER BY edateyear ASC, edatemonth ASC, edateday ASC")
    fun getAllExam(yid: Int): LiveData<List<ExamSubjectYearExamtype>>

    @Transaction
    @Query("SELECT * FROM exam INNER JOIN subject ON subject.sid = exam.e_sid INNER JOIN year ON year.yid = exam.e_yid INNER JOIN examtype ON examtype.etid = exam.e_etid WHERE e_yid=:yid AND egrade = -1 ORDER BY edateyear ASC, edatemonth ASC, edateday ASC")
    fun getAllExamPending(yid: Int): LiveData<List<ExamSubjectYearExamtype>>

    @Transaction
    @Query("SELECT * FROM exam INNER JOIN subject ON subject.sid = exam.e_sid INNER JOIN year ON year.yid = exam.e_yid INNER JOIN examtype ON examtype.etid = exam.e_etid WHERE e_yid=:yid AND e_sid=:sid ORDER BY edateyear ASC, edatemonth ASC, edateday ASC")
    fun getAllExamBySubject(yid: Int, sid: Int): LiveData<List<ExamSubjectYearExamtype>>

    @Transaction
    @Query("SELECT * FROM exam INNER JOIN subject ON subject.sid = exam.e_sid INNER JOIN year ON year.yid = exam.e_yid INNER JOIN examtype ON examtype.etid = exam.e_etid WHERE e_yid=:yid AND e_sid=:sid AND egrade = -1 ORDER BY edateyear ASC, edatemonth ASC, edateday ASC")
    fun getAllExamPendingBySubject(yid: Int, sid: Int): LiveData<List<ExamSubjectYearExamtype>>

    @Transaction
    @Query("SELECT * FROM exam INNER JOIN subject ON subject.sid = exam.e_sid INNER JOIN year ON year.yid = exam.e_yid INNER JOIN examtype ON examtype.etid = exam.e_etid WHERE e_yid=:yid ORDER BY subject.sname ASC, edateyear ASC, edatemonth ASC, edateday ASC")
    fun getAllExamByOrderSubject(yid: Int): LiveData<List<ExamSubjectYearExamtype>>

    @Transaction
    @Query("SELECT * FROM exam INNER JOIN subject ON subject.sid = exam.e_sid INNER JOIN year ON year.yid = exam.e_yid INNER JOIN examtype ON examtype.etid = exam.e_etid WHERE e_yid=:yid ORDER BY examtype.etname ASC,edateyear ASC, edatemonth ASC, edateday ASC")
    fun getAllExamByOrderExamtype(yid: Int): LiveData<List<ExamSubjectYearExamtype>>

    @Transaction
    @Query("SELECT * FROM exam INNER JOIN subject ON subject.sid = exam.e_sid INNER JOIN year ON year.yid = exam.e_yid INNER JOIN examtype ON examtype.etid = exam.e_etid WHERE e_yid=:yid ORDER BY egrade ASC, edateyear ASC, edatemonth ASC, edateday ASC")
    fun getAllExamByOrderGrade(yid: Int): LiveData<List<ExamSubjectYearExamtype>>

    @Transaction
    @Query("SELECT * FROM exam INNER JOIN subject ON subject.sid = exam.e_sid INNER JOIN year ON year.yid = exam.e_yid INNER JOIN examtype ON examtype.etid = exam.e_etid WHERE e_yid=:yid AND sid=:sid ORDER BY subject.sname ASC, edateyear ASC, edatemonth ASC, edateday ASC")
    fun getAllExamBySubjectOrderSubject(yid: Int, sid: Int): LiveData<List<ExamSubjectYearExamtype>>

    @Transaction
    @Query("SELECT * FROM exam INNER JOIN subject ON subject.sid = exam.e_sid INNER JOIN year ON year.yid = exam.e_yid INNER JOIN examtype ON examtype.etid = exam.e_etid WHERE e_yid=:yid AND sid=:sid ORDER BY examtype.etname ASC, edateyear ASC, edatemonth ASC, edateday ASC")
    fun getAllExamBySubjectOrderExamtype(yid: Int, sid: Int): LiveData<List<ExamSubjectYearExamtype>>

    @Transaction
    @Query("SELECT * FROM exam INNER JOIN subject ON subject.sid = exam.e_sid INNER JOIN year ON year.yid = exam.e_yid INNER JOIN examtype ON examtype.etid = exam.e_etid WHERE e_yid=:yid AND sid=:sid ORDER BY egrade ASC, edateyear ASC, edatemonth ASC, edateday ASC")
    fun getAllExamBySubjectOrderGrade(yid: Int, sid: Int): LiveData<List<ExamSubjectYearExamtype>>

    @Transaction
    @Query("SELECT * FROM exam INNER JOIN subject ON subject.sid = exam.e_sid INNER JOIN year ON year.yid = exam.e_yid INNER JOIN examtype ON examtype.etid = exam.e_etid WHERE e_yid=:yid AND e_sid=:sid ORDER BY edateyear ASC, edatemonth ASC, edateday ASC")
    fun getSubjectExams(yid: Int, sid: Int): LiveData<List<ExamSubjectYearExamtype>>

    @Transaction
    @Query("SELECT * FROM exam INNER JOIN subject ON subject.sid = exam.e_sid INNER JOIN year ON year.yid = exam.e_yid INNER JOIN examtype ON examtype.etid = exam.e_etid WHERE e_yid=:yid AND e_sid=:sid ORDER BY edateyear ASC, edatemonth ASC, edateday ASC")
    suspend fun getSubjectExamsSuspend(yid: Int, sid: Int): List<ExamSubjectYearExamtype>

    @Transaction
    @Query("SELECT * FROM exam INNER JOIN subject ON subject.sid = exam.e_sid INNER JOIN year ON year.yid = exam.e_yid INNER JOIN examtype ON examtype.etid = exam.e_etid WHERE e_yid=:yid  ORDER BY edateyear ASC, edatemonth ASC, edateday ASC")
    fun getAllExamSuspend(yid: Int): List<ExamSubjectYearExamtype>

}