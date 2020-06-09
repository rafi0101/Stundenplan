package com.ebner.stundenplan.database.table.examtype

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by raphael on 25.05.2020.
 * Stundenplan Created in com.ebner.stundenplan.database.table.examtype
 */
@Entity(tableName = "examtype")
data class Examtype(

        @ColumnInfo(name = "etname")
        var etname: String,

        @ColumnInfo(name = "etweight")
        var etweight: Double,

        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "etid")
        var etid: Int = 0

)
