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

        @ColumnInfo(name = "yname")
        var yname: String,

        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "yid")
        var yid: Int = 0

) {
    override fun toString(): String {
        return yname
    }
}