package com.ebner.stundenplan.database.table.settings

import androidx.lifecycle.LiveData
import com.ebner.stundenplan.database.table.mergedEntities.SettingsYear

/**
 * Created by raphael on 26.05.2020.
 * Stundenplan Created in com.ebner.stundenplan.database.table.settings
 */
class SettingsRepository(
        private val settingsDao: SettingsDao
) {
    /*---------------------Just pass the queries to the DAO--------------------------*/
    //fun insert(settings: Settings) = settingsDao.insert(settings)
    fun update(settings: Settings) = settingsDao.update(settings)
    //fun delete(settings: Settings) = settingsDao.delete(settings)

    //Live data view
    val getSettings: LiveData<SettingsYear> = settingsDao.getSettings()


}