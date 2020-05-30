package com.ebner.stundenplan.database.table.subject

import androidx.lifecycle.LiveData
import com.ebner.stundenplan.database.table.mergedEntities.SubjectTeacherRoom

/**
 * Created by raphael on 21.05.2020.
 * Stundenplan Created in com.ebner.stundenplan.database.table.subject
 */
class SubjectRepository(
        private val subjectDao: SubjectDao
) {
    /*---------------------Just pass the queries to the DAO--------------------------*/
    fun insert(subject: Subject) = subjectDao.insert(subject)
    fun update(subject: Subject) = subjectDao.update(subject)
    fun delete(subject: Subject) = subjectDao.delete(subject)

    //Live data view
    val getAllSubject: LiveData<List<SubjectTeacherRoom>> = subjectDao.getAllSubject()

    suspend fun getAllSubjectList(): List<Subject> = subjectDao.getAllSubjectList()
}