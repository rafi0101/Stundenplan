package com.ebner.stundenplan.database.table.lesson

import androidx.room.*
import com.ebner.stundenplan.database.table.schoolLesson.SchoolLesson
import com.ebner.stundenplan.database.table.subject.Subject
import com.ebner.stundenplan.database.table.year.Year

/**
 * Created by raphael on 07.06.2020.
 * Stundenplan Created in com.ebner.stundenplan.database.table.lessons
 */
@Entity(
        tableName = "lesson",
        foreignKeys = arrayOf(
                ForeignKey(entity = SchoolLesson::class, parentColumns = arrayOf("slid"), childColumns = arrayOf("l_slid"), onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.SET_DEFAULT),
                ForeignKey(entity = Subject::class, parentColumns = arrayOf("sid"), childColumns = arrayOf("l_sid"), onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.SET_DEFAULT),
                ForeignKey(entity = Year::class, parentColumns = arrayOf("yid"), childColumns = arrayOf("l_yid"), onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.SET_DEFAULT)
        ),
        indices = arrayOf(
                Index("l_slid"),
                Index("l_sid"),
                Index("l_yid")
        )
)
data class Lesson(

        @ColumnInfo(name = "lday")
        var lday: Int,

        @ColumnInfo(name = "l_slid")
        var lslid: Int,

        @ColumnInfo(name = "l_sid")
        var lsid: Int,

        @ColumnInfo(name = "l_yid")
        var lyid: Int

) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "lid")
    var lid: Int = 0

}