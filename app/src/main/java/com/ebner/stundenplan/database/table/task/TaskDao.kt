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
    @Query("SELECT * FROM task INNER JOIN lesson ON task.tk_lid = lesson.lid INNER JOIN schoollesson ON lesson.l_slid = schoollesson.slid INNER JOIN subject ON lesson.l_sid = subject.sid INNER JOIN teacher ON subject.s_tid = teacher.tid INNER JOIN room ON subject.s_rid = room.rid INNER JOIN year ON task.tk_yid = year.yid WHERE task.tk_yid=:yid ORDER BY tk_name ASC")
    fun getAllTask(yid: Int): LiveData<List<TaskLesson>>


    @Query("SELECT * FROM task WHERE task.tk_yid=:yid ORDER BY tk_name ASC")
    suspend fun getAllTaskList(yid: Int): List<Task>

}