package com.ebner.stundenplan.database.table.schoolLesson

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.ebner.stundenplan.database.main.StundenplanDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by raphael on 07.06.2020.
 * Stundenplan Created in com.ebner.stundenplan.database.table.schoolLesson
 */
class SchoolLessonViewModel(application: Application) : AndroidViewModel(application) {

    private val schoolLessonRepository: SchoolLessonRepository

    val allSchoolLesson: LiveData<List<SchoolLesson>>

    /*---------------------Define the Database, and the Repository--------------------------*/
    init {
        val schoolLessonDao = StundenplanDatabase.getInstance(application).schoolLessonDao()
        schoolLessonRepository = SchoolLessonRepository(schoolLessonDao)
        allSchoolLesson = schoolLessonRepository.getAllSchoolLesson

    }

    /*---------------------Define default queries--------------------------*/
    fun insert(schoolLesson: SchoolLesson) = viewModelScope.launch(Dispatchers.IO) {
        schoolLessonRepository.insert(schoolLesson)
    }

    fun update(schoolLesson: SchoolLesson) = viewModelScope.launch(Dispatchers.IO) {
        schoolLessonRepository.update(schoolLesson)
    }

    fun delete(schoolLesson: SchoolLesson) = viewModelScope.launch(Dispatchers.IO) {
        schoolLessonRepository.delete(schoolLesson)
    }


}