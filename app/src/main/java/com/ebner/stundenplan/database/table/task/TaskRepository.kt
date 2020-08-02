package com.ebner.stundenplan.database.table.task

import androidx.lifecycle.LiveData
import com.ebner.stundenplan.database.table.mergedEntities.TaskLesson

/**
 * Created by raphael on 02.07.2020.
 * Stundenplan Created in com.ebner.stundenplan.database.table.task
 */
class TaskRepository(
        private val taskDao: TaskDao
) {
    /*---------------------Just pass the queries to the DAO--------------------------*/
    fun insert(task: Task) = taskDao.insert(task)
    fun update(task: Task) = taskDao.update(task)
    fun delete(task: Task) = taskDao.delete(task)

    //Live data view
    fun getAllTask(yid: Int): LiveData<List<TaskLesson>> = taskDao.getAllTask(yid)

}