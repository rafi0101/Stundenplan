package com.ebner.stundenplan.database.table.teacher

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by raphael on 15.05.2020.
 * Stundenplan Created in com.ebner.stundenplan.database.table
 */

@Entity(tableName = "teacher")
data class Teacher(

        @ColumnInfo(name = "tname")
        var tname: String,

        @ColumnInfo(name = "tgender")
        var tgender: Int,
        /*
         * 0 == Herr
         * 1 == Frau
         */

        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "tid")
        var tid: Int = 0

)