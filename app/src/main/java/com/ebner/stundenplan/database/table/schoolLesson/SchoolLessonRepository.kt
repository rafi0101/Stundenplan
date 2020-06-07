package com.ebner.stundenplan.database.table.schoolLesson

import androidx.lifecycle.LiveData

/**
 * Created by raphael on 07.06.2020.
 * Stundenplan Created in com.ebner.stundenplan.database.table.schoolLesson
 */
class SchoolLessonRepository(
        private val schoolLessonDao: SchoolLessonDao
) {
    /*---------------------Just pass the queries to the DAO--------------------------*/
    fun insert(schoolLesson: SchoolLesson) = schoolLessonDao.insert(schoolLesson)
    fun update(schoolLesson: SchoolLesson) = schoolLessonDao.update(schoolLesson)
    fun delete(schoolLesson: SchoolLesson) = schoolLessonDao.delete(schoolLesson)

    //Live data view
    val getAllSchoolLesson: LiveData<List<SchoolLesson>> = schoolLessonDao.getAllSchoolLesson()

}