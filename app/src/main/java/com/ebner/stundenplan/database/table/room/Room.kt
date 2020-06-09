package com.ebner.stundenplan.database.table.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by raphael on 17.05.2020.
 * Stundenplan Created in com.ebner.stundenplan.database.table.room
 */

@Entity(tableName = "room")
data class Room(

        @ColumnInfo(name = "rname")
        var rname: String,

        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "rid")
        var rid: Int = 0

)