package com.ebner.stundenplan.database.table.subject

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.ebner.stundenplan.database.table.BaseDao
import com.ebner.stundenplan.database.table.mergedEntities.SubjectTeacherRoom

/**
 * Created by raphael on 21.05.2020.
 * Stundenplan Created in com.ebner.stundenplan.database.table.subject
 */
@Dao
interface SubjectDao : BaseDao<Subject> {

    @Transaction
    @Query("SELECT * FROM subject INNER JOIN teacher ON teacher.tid = subject.s_tid INNER JOIN room ON room.rid = subject.s_rid ORDER BY sinactive ASC, sname")
    fun getAllSubject(): LiveData<List<SubjectTeacherRoom>>

    @Query("SELECT * FROM subject WHERE sinactive = 0 ORDER BY sname ASC")
    suspend fun getAllSubjectList(): List<Subject>

    @Query("SELECT * FROM subject WHERE sid=:sid")
    suspend fun getSubjectByID(sid: Int): Subject
}