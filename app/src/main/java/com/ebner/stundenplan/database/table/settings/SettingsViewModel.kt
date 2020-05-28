package com.ebner.stundenplan.database.table.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.ebner.stundenplan.database.main.StundenplanDatabase
import com.ebner.stundenplan.database.table.mergedEntities.SettingsYear
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by raphael on 26.05.2020.
 * Stundenplan Created in com.ebner.stundenplan.database.table.settings
 */
class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsRepository: SettingsRepository

    val allSettings: LiveData<SettingsYear>

    /*---------------------Define the Database, and the Repository--------------------------*/
    init {
        val settingsDao = StundenplanDatabase.getInstance(application).settingsDao()
        settingsRepository = SettingsRepository(settingsDao)
        allSettings = settingsRepository.getSettings


    }


    /*---------------------Define default queries--------------------------*/
    fun update(settings: Settings) = viewModelScope.launch(Dispatchers.IO) {
        settingsRepository.update(settings)
    }


}