package com.ebner.stundenplan.database.table.schoolLesson

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by raphael on 07.06.2020.
 * Stundenplan Created in com.ebner.stundenplan.database.table.schoolLessons
 */
@Entity(tableName = "schoollesson")
data class SchoolLesson(

        @ColumnInfo(name = "slnumber")
        var slnumber: Int,

        @ColumnInfo(name = "slstarthour")
        var slstarthour: Int,

        @ColumnInfo(name = "slstartminute")
        var slstartminute: Int,

        @ColumnInfo(name = "slendhour")
        var slendhour: Int,

        @ColumnInfo(name = "slendminute")
        var slendminute: Int,

        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "slid")
        var slid: Int = 0
)