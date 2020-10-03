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
        foreignKeys = [
            ForeignKey(entity = SchoolLesson::class, parentColumns = ["slid"], childColumns = ["l_slid"], onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.SET_DEFAULT),
            ForeignKey(entity = Subject::class, parentColumns = ["sid"], childColumns = ["l_sid"], onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.SET_DEFAULT),
            ForeignKey(entity = Year::class, parentColumns = ["yid"], childColumns = ["l_yid"], onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.SET_DEFAULT)
        ],
        indices = [
            Index("l_slid"),
            Index("l_sid"),
            Index("l_yid")
        ]
)
data class Lesson(

        @ColumnInfo(name = "lday")
        var lday: Int,

        @ColumnInfo(name = "lcycle")
        var lcycle: Int,
        /**
         * -1 = Disabled / Both
         * 1 = A Only
         * 2 = B Only
         */

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