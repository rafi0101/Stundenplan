package com.ebner.stundenplan.database.table.teacher

import androidx.lifecycle.LiveData

/**
 * Created by raphael on 21.05.2020.
 * Stundenplan Created in com.ebner.stundenplan.database.table.teacher
 */
class TeacherRepository(
        private val teacherDao: TeacherDao
) {
    /*---------------------Just pass the queries to the DAO--------------------------*/
    fun insert(teacher: Teacher) = teacherDao.insert(teacher)
    fun update(teacher: Teacher) = teacherDao.update(teacher)
    fun delete(teacher: Teacher) = teacherDao.delete(teacher)

    //Live data view
    val getAllTeacher: LiveData<List<Teacher>> = teacherDao.getAllTeacher()

    suspend fun getAllTeacherList(): List<Teacher> = teacherDao.getAllTeacherList()
}