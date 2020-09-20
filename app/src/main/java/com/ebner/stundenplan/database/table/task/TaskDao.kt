package com.ebner.stundenplan.database.table.task

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.ebner.stundenplan.database.table.BaseDao
import com.ebner.stundenplan.database.table.mergedEntities.TaskLesson

/**
 * Created by raphael on 02.07.2020.
 * Stundenplan Created in com.ebner.stundenplan.database.table.task
 */
@Dao
interface TaskDao : BaseDao<Task> {

    @Transaction
    @Query("SELECT * FROM task INNER JOIN lesson ON task.tk_lid = lesson.lid INNER JOIN schoollesson ON lesson.l_slid = schoollesson.slid INNER JOIN subject ON lesson.l_sid = subject.sid INNER JOIN teacher ON subject.s_tid = teacher.tid INNER JOIN room ON subject.s_rid = room.rid INNER JOIN year ON task.tk_yid = year.yid WHERE task.tk_yid=:yid ORDER BY tkdateyear ASC, tkdatemonth ASC, tkdateday ASC")
    fun getAllTask(yid: Int): LiveData<List<TaskLesson>>

    @Transaction
    @Query("SELECT * FROM task INNER JOIN lesson ON task.tk_lid = lesson.lid INNER JOIN schoollesson ON lesson.l_slid = schoollesson.slid INNER JOIN subject ON lesson.l_sid = subject.sid INNER JOIN teacher ON subject.s_tid = teacher.tid INNER JOIN room ON subject.s_rid = room.rid INNER JOIN year ON task.tk_yid = year.yid WHERE task.tk_yid=:yid AND lesson.l_sid=:sid ORDER BY tkdateyear ASC, tkdatemonth ASC, tkdateday ASC")
    fun getAllTaskBySubject(yid: Int, sid: Int): LiveData<List<TaskLesson>>

    @Transaction
    @Query("SELECT * FROM task INNER JOIN lesson ON task.tk_lid = lesson.lid INNER JOIN schoollesson ON lesson.l_slid = schoollesson.slid INNER JOIN subject ON lesson.l_sid = subject.sid INNER JOIN teacher ON subject.s_tid = teacher.tid INNER JOIN room ON subject.s_rid = room.rid INNER JOIN year ON task.tk_yid = year.yid WHERE task.tk_yid=:yid AND tkfinished=:finished ORDER BY tkdateyear ASC, tkdatemonth ASC, tkdateday ASC")
    fun getAllTaskByFinished(yid: Int, finished: Boolean): LiveData<List<TaskLesson>>

    @Transaction
    @Query("SELECT * FROM task INNER JOIN lesson ON task.tk_lid = lesson.lid INNER JOIN schoollesson ON lesson.l_slid = schoollesson.slid INNER JOIN subject ON lesson.l_sid = subject.sid INNER JOIN teacher ON subject.s_tid = teacher.tid INNER JOIN room ON subject.s_rid = room.rid INNER JOIN year ON task.tk_yid = year.yid WHERE task.tk_yid=:yid AND lesson.l_sid=:sid AND tkfinished=:finished ORDER BY tkdateyear ASC, tkdatemonth ASC, tkdateday ASC")
    fun getAllTaskBySubjectFinished(yid: Int, sid: Int, finished: Boolean): LiveData<List<TaskLesson>>

    @Transaction
    @Query("SELECT * FROM task INNER JOIN lesson ON lesson.lid = task.tk_lid INNER JOIN subject ON subject.sid = lesson.l_sid WHERE tk_yid=:yid AND subject.sid=:sid ORDER BY tkdateyear ASC, tkdatemonth ASC, tkdateday ASC")
    fun getSubjectTasks(yid: Int, sid: Int): LiveData<List<Task>>


    @Query("SELECT * FROM task WHERE task.tk_yid=:yid ORDER BY tkname ASC")
    suspend fun getAllTaskList(yid: Int): List<Task>

}