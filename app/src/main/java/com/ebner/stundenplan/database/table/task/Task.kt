package com.ebner.stundenplan.database.table.task

import androidx.room.*
import com.ebner.stundenplan.database.table.lesson.Lesson
import com.ebner.stundenplan.database.table.year.Year

/**
 * Created by raphael on 02.07.2020.
 * Stundenplan Created in com.ebner.stundenplan.database.table.task
 */
@Entity(
        tableName = "task",
        foreignKeys = [
            ForeignKey(entity = Lesson::class, parentColumns = ["lid"], childColumns = ["tk_lid"], onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.SET_DEFAULT),
            ForeignKey(entity = Year::class, parentColumns = ["yid"], childColumns = ["tk_yid"], onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.SET_DEFAULT)
        ],
        indices = [
            Index("tk_lid"),
            Index("tk_yid")
        ]
)
data class Task(

        @ColumnInfo(name = "tkname")
        var tkname: String,

        @ColumnInfo(name = "tknote")
        var tknote: String,

        @ColumnInfo(name = "tkdateday")
        var tkdateday: Int,

        @ColumnInfo(name = "tkdatemonth")
        var tkdatemonth: Int,

        @ColumnInfo(name = "tkdateyear")
        var tkdateyear: Int,

        @ColumnInfo(name = "tkfinished")
        var tkfinished: Boolean,

        @ColumnInfo(name = "tk_lid")
        var tklid: Int,

        @ColumnInfo(name = "tk_yid")
        var tkyid: Int

) {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "tkid")
    var tkid: Int = 0
}