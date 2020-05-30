package com.ebner.stundenplan.database.table.exam

import androidx.room.*
import com.ebner.stundenplan.database.table.examtype.Examtype
import com.ebner.stundenplan.database.table.subject.Subject
import com.ebner.stundenplan.database.table.year.Year

/**
 * Created by raphael on 29.05.2020.
 * Stundenplan Created in com.ebner.stundenplan.database.table.exam
 */
@Entity(
        tableName = "exam",
        foreignKeys = arrayOf(
                ForeignKey(entity = Subject::class, parentColumns = arrayOf("sid"), childColumns = arrayOf("e_sid"), onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.SET_DEFAULT),
                ForeignKey(entity = Examtype::class, parentColumns = arrayOf("etid"), childColumns = arrayOf("e_etid"), onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.SET_DEFAULT),
                ForeignKey(entity = Year::class, parentColumns = arrayOf("yid"), childColumns = arrayOf("e_yid"), onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.SET_DEFAULT)
        ),
        indices = arrayOf(
                Index("e_sid"),
                Index("e_etid"),
                Index("e_yid")
        )
)
data class Exam(

        @ColumnInfo(name = "e_sid")
        var esid: Int,

        @ColumnInfo(name = "e_etid")
        var eetid: Int,

        @ColumnInfo(name = "e_yid")
        var eyid: Int,

        @ColumnInfo(name = "egrade")
        var egrade: Int,

        @ColumnInfo(name = "edateyear")
        var edateyear: Int,

        @ColumnInfo(name = "edatemonth")
        var edatemonth: Int,

        @ColumnInfo(name = "edateday")
        var edateday: Int


) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "eid")
    var eid: Int = 0


}