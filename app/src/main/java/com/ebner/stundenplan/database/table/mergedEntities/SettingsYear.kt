package com.ebner.stundenplan.database.table.mergedEntities

import androidx.room.Embedded
import com.ebner.stundenplan.database.table.settings.Settings
import com.ebner.stundenplan.database.table.year.Year


/**
 * Created by raphael on 26.05.2020.
 * Stundenplan Created in com.ebner.stundenplan.database.table.mergedEntities
 */

/**
 * This class captures the relationship between [Settings] and [Year]
 */

data class SettingsYear(

        @Embedded
        var settings: Settings,

        @Embedded
        var year: Year

)