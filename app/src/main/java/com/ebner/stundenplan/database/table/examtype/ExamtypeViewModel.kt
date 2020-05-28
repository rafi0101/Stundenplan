package com.ebner.stundenplan.database.table.examtype

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.ebner.stundenplan.database.main.StundenplanDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by raphael on 25.05.2020.
 * Stundenplan Created in com.ebner.stundenplan.database.table.examtype
 */
class ExamtypeViewModel(application: Application) : AndroidViewModel(application) {

    private val examtypeRepository: ExamtypeRepository

    val allExamtype: LiveData<List<Examtype>>

    /*---------------------Define the Database, and the Repository--------------------------*/
    init {
        val examtypeDao = StundenplanDatabase.getInstance(application).examtypeDao()
        examtypeRepository = ExamtypeRepository(examtypeDao)
        allExamtype = examtypeRepository.getAllExamtype

    }

    /*---------------------Define default queries--------------------------*/
    fun insert(examtype: Examtype) = viewModelScope.launch(Dispatchers.IO) {
        examtypeRepository.insert(examtype)
    }

    fun update(examtype: Examtype) = viewModelScope.launch(Dispatchers.IO) {
        examtypeRepository.update(examtype)
    }

    fun delete(examtype: Examtype) = viewModelScope.launch(Dispatchers.IO) {
        examtypeRepository.delete(examtype)
    }


}