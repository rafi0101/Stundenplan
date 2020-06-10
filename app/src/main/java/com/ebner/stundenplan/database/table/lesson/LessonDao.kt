package com.ebner.stundenplan.database.table.lesson

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.ebner.stundenplan.database.table.BaseDao
import com.ebner.stundenplan.database.table.mergedEntities.LessonSubjectSchoollessonYear

/**
 * Created by raphael on 07.06.2020.
 * Stundenplan Created in com.ebner.stundenplan.database.table.lesson
 */
@Dao
interface LessonDao : BaseDao<Lesson> {

    @Transaction
    @Query("SELECT * FROM lesson INNER JOIN subject ON subject.sid = lesson.l_sid INNER JOIN room ON room.rid = subject.s_rid  INNER JOIN teacher ON teacher.tid = subject.s_tid INNER JOIN year ON year.yid = lesson.l_yid INNER JOIN schoollesson ON schoollesson.slid = lesson.l_slid WHERE l_yid=:yid")
    fun getAllLesson(yid: Int): LiveData<List<LessonSubjectSchoollessonYear>>

    @Transaction
    @Query("SELECT * FROM lesson INNER JOIN subject ON subject.sid = lesson.l_sid INNER JOIN room ON room.rid = subject.s_rid  INNER JOIN teacher ON teacher.tid = subject.s_tid INNER JOIN year ON year.yid = lesson.l_yid INNER JOIN schoollesson ON schoollesson.slid = lesson.l_slid WHERE lid=:lid")
    suspend fun getLesson(lid: Int): LessonSubjectSchoollessonYear

}