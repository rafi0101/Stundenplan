package com.ebner.stundenplan.database.table.lesson

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.ebner.stundenplan.database.main.StundenplanDatabase
import com.ebner.stundenplan.database.table.mergedEntities.LessonSubjectSchoollessonYear
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by raphael on 07.06.2020.
 * Stundenplan Created in com.ebner.stundenplan.database.table.lesson
 */
class LessonViewModel(application: Application) : AndroidViewModel(application) {

    private val lessonRepository: LessonRepository


    /*---------------------Define the Database, and the Repository--------------------------*/
    init {
        val lessonDao = StundenplanDatabase.getInstance(application).lessonDao()
        lessonRepository = LessonRepository(lessonDao)

    }

    /*---------------------Define default queries--------------------------*/
    fun insert(lesson: Lesson) = viewModelScope.launch(Dispatchers.IO) {
        lessonRepository.insert(lesson)
    }

    fun update(lesson: Lesson) = viewModelScope.launch(Dispatchers.IO) {
        lessonRepository.update(lesson)
    }

    fun delete(lesson: Lesson) = viewModelScope.launch(Dispatchers.IO) {
        lessonRepository.delete(lesson)
    }

    fun allLesson(yid: Int): LiveData<List<LessonSubjectSchoollessonYear>> = lessonRepository.getAllLesson(yid)

    suspend fun singleLesson(lid: Int): LessonSubjectSchoollessonYear = lessonRepository.getLesson(lid)

    suspend fun lessonBySubject(sid: Int): List<Lesson> = lessonRepository.getLessonBySubject(sid)
    suspend fun lessonBySubjectDay(sid: Int, day: Int): List<LessonSubjectSchoollessonYear> = lessonRepository.getLessonbySubjectDay(sid, day)
    suspend fun lessonbySubjectDaySchoollesson(sid: Int, day: Int, slid: Int): List<Lesson> = lessonRepository.getLessonbySubjectDaySchoollesson(sid, day, slid)

    suspend fun allLessonList(): List<Lesson> {
        return lessonRepository.getAllLessonList()
    }

}