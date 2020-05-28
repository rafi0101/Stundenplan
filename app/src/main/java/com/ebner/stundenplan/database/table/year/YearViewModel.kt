package com.ebner.stundenplan.database.table.year

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.ebner.stundenplan.database.main.StundenplanDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by raphael on 25.05.2020.
 * Stundenplan Created in com.ebner.stundenplan.database.table.year
 */
class YearViewModel(application: Application) : AndroidViewModel(application) {

    private val yearRepository: YearRepository

    val allYear: LiveData<List<Year>>

    /*---------------------Define the Database, and the Repository--------------------------*/
    init {
        val yearDao = StundenplanDatabase.getInstance(application).yearDao()
        yearRepository = YearRepository(yearDao)
        allYear = yearRepository.getAllYear

    }

    /*---------------------Define default queries--------------------------*/
    fun insert(year: Year) = viewModelScope.launch(Dispatchers.IO) {
        yearRepository.insert(year)
    }

    fun update(year: Year) = viewModelScope.launch(Dispatchers.IO) {
        yearRepository.update(year)
    }

    fun delete(year: Year) = viewModelScope.launch(Dispatchers.IO) {
        yearRepository.delete(year)
    }

    suspend fun allYearList(): List<Year> {
        return yearRepository.getAllYearList()
    }

}