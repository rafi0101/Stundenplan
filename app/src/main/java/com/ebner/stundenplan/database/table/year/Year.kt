package com.ebner.stundenplan.database.table.year

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by raphael on 25.05.2020.
 * Stundenplan Created in com.ebner.stundenplan.database.table.year
 */
@Entity(tableName = "year")
data class Year(

        /**
         * needs to contain each field in a table, except the PK But when the PK is used in another class ([SubjectTeacherRoom]), it needs to be in this area
         */

        @ColumnInfo(name = "yname")
        var yname: String,

        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "yid")
        var yid: Int = 0

) {
}