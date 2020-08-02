package com.ebner.stundenplan.database.table.lesson

import androidx.lifecycle.LiveData
import com.ebner.stundenplan.database.table.mergedEntities.LessonSubjectSchoollessonYear

/**
 * Created by raphael on 07.06.2020.
 * Stundenplan Created in com.ebner.stundenplan.database.table.lesson
 */
class LessonRepository(
        private val lessonDao: LessonDao
) {
    /*---------------------Just pass the queries to the DAO--------------------------*/
    fun insert(lesson: Lesson) = lessonDao.insert(lesson)
    fun update(lesson: Lesson) = lessonDao.update(lesson)
    fun delete(lesson: Lesson) = lessonDao.delete(lesson)

    //Live data view
    fun getAllLesson(yid: Int): LiveData<List<LessonSubjectSchoollessonYear>> = lessonDao.getAllLesson(yid)

    suspend fun getLesson(lid: Int): LessonSubjectSchoollessonYear = lessonDao.getLesson(lid)
    suspend fun getAllLessonList(): List<Lesson> = lessonDao.getAllLessonList()
    suspend fun getLessonBySubject(sid: Int): List<Lesson> = lessonDao.getLessonBySubject(sid)
    suspend fun getLessonbySubjectDay(sid: Int, day: Int): List<LessonSubjectSchoollessonYear> = lessonDao.getLessonBySubjectDay(sid, day)
    suspend fun getLessonbySubjectDaySchoollesson(sid: Int, day: Int, slid: Int): List<Lesson> = lessonDao.getLessonbySubjectDaySchoollesson(sid, day, slid)

}