package com.ebner.stundenplan.database.table.settings

import androidx.room.*
import com.ebner.stundenplan.database.table.year.Year


/**
 * Created by raphael on 26.05.2020.
 * Stundenplan Created in com.ebner.stundenplan.database.table.settings
 */
@Entity(
        tableName = "settings",
        foreignKeys = arrayOf(
                ForeignKey(entity = Year::class, parentColumns = arrayOf("yid"), childColumns = arrayOf("set_yid"), onDelete = ForeignKey.SET_DEFAULT, onUpdate = ForeignKey.SET_DEFAULT)
        ),
        indices = arrayOf(
                Index("set_yid")
        )
)
data class Settings(

        //This is the ForeignKey refers to the Years table
        @ColumnInfo(name = "set_yid")
        var setyid: Int,

        @PrimaryKey
        @ColumnInfo(name = "setid")
        var setid: Int = 1


)