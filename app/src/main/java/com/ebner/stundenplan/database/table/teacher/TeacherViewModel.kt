package com.ebner.stundenplan.database.table.teacher

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.ebner.stundenplan.database.main.StundenplanDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by raphael on 16.05.2020.
 * Stundenplan Created in com.ebner.stundenplan.database.table.teacher
 */
class TeacherViewModel(application: Application) : AndroidViewModel(application) {

    private val teacherRepository: TeacherRepository

    val allTeacher: LiveData<List<Teacher>>

    /*---------------------Define the Database, and the Repository--------------------------*/
    init {
        val teacherDao = StundenplanDatabase.getInstance(application).teacherDao()
        teacherRepository = TeacherRepository(teacherDao)
        allTeacher = teacherRepository.getAllTeacher

    }

    /*---------------------Define default queries--------------------------*/
    fun insert(teacher: Teacher) = viewModelScope.launch(Dispatchers.IO) {
        teacherRepository.insert(teacher)
    }

    fun update(teacher: Teacher) = viewModelScope.launch(Dispatchers.IO) {
        teacherRepository.update(teacher)
    }

    fun delete(teacher: Teacher) = viewModelScope.launch(Dispatchers.IO) {
        teacherRepository.delete(teacher)
    }

    suspend fun allTeacherList(): List<Teacher> {
        return teacherRepository.getAllTeacherList()
    }

}