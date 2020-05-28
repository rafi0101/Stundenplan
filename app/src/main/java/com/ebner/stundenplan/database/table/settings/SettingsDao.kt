package com.ebner.stundenplan.database.table.settings

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ebner.stundenplan.database.table.mergedEntities.SettingsYear

/**
 * Created by raphael on 26.05.2020.
 * Stundenplan Created in com.ebner.stundenplan.database.table.settings
 */
@Dao
interface SettingsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(settings: Settings)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(settings: Settings)

    @Transaction
    @Query("SELECT * FROM settings INNER JOIN year ON year.yid = settings.set_yid WHERE setid = 1")
    fun getSettings(): LiveData<SettingsYear>

}