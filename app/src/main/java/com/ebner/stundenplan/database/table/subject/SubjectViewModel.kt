package com.ebner.stundenplan.database.table.subject

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.ebner.stundenplan.database.main.StundenplanDatabase
import com.ebner.stundenplan.database.table.mergedEntities.SubjectTeacherRoom
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 * Created by raphael on 21.05.2020.
 * Stundenplan Created in com.ebner.stundenplan.database.table.subject
 */
class SubjectViewModel(application: Application) : AndroidViewModel(application) {

    private val subjectRepository: SubjectRepository

    val allSubject: LiveData<List<SubjectTeacherRoom>>

    /*---------------------Define the Database, and the Repository--------------------------*/
    init {
        val subjectDao = StundenplanDatabase.getInstance(application).subjectDao()
        subjectRepository = SubjectRepository(subjectDao)
        allSubject = subjectRepository.getAllSubject

    }

    /*---------------------Define default queries--------------------------*/
    fun insert(subject: Subject) = viewModelScope.launch(Dispatchers.IO) {
        subjectRepository.insert(subject)
    }

    fun update(subject: Subject) = viewModelScope.launch(Dispatchers.IO) {
        subjectRepository.update(subject)
    }

    fun delete(subject: Subject) = viewModelScope.launch(Dispatchers.IO) {
        subjectRepository.delete(subject)
    }

    suspend fun allSubjectList(): List<Subject> {
        return subjectRepository.getAllSubjectList()
    }

    suspend fun subjectByID(sid: Int): Subject = subjectRepository.getSubjectByID(sid)


}
