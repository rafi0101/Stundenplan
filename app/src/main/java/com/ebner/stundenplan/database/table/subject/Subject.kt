package com.ebner.stundenplan.database.table.subject


import androidx.room.*
import com.ebner.stundenplan.database.table.room.Room
import com.ebner.stundenplan.database.table.teacher.Teacher

/**
 * Created by raphael on 21.05.2020.
 * Stundenplan Created in com.ebner.stundenplan.database.table.subject
 */
@Entity(
        tableName = "subject",
        foreignKeys = arrayOf(
                ForeignKey(entity = Teacher::class, parentColumns = arrayOf("tid"), childColumns = arrayOf("s_tid"), onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.SET_DEFAULT),
                ForeignKey(entity = Room::class, parentColumns = arrayOf("rid"), childColumns = arrayOf("s_rid"), onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.SET_DEFAULT)
        ),
        indices = arrayOf(
                Index("s_tid"),
                Index("s_rid")
        )
)
data class Subject(

        @ColumnInfo(name = "sname")
        var sname: String,

        @ColumnInfo(name = "snameshort")
        var snameshort: String,

        @ColumnInfo(name = "scolor")
        var scolor: Int,

        @ColumnInfo(name = "snote")
        var snote: String,

        @ColumnInfo(name = "sinactive")
        var sinactive: Boolean,

        //This is the ForeignKey; s_: item of subject, but ForeignKey "tid" the id from other table
        @ColumnInfo(name = "s_tid")
        var stid: Int,

        //This is the ForeignKey; s_: item of subject, but ForeignKey "tid" the id from other table
        @ColumnInfo(name = "s_rid")
        var srid: Int

) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "sid")
    var sid: Int = 0


}