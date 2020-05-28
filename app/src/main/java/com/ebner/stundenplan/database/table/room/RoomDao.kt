package com.ebner.stundenplan.database.table.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.ebner.stundenplan.database.table.BaseDao

/**
 * Created by raphael on 17.05.2020.
 * Stundenplan Created in com.ebner.stundenplan.database.table.room
 */
@Dao
interface RoomDao : BaseDao<Room> {


    @Query("SELECT * FROM Room ORDER BY rname ASC")
    fun getAllRoom(): LiveData<List<Room>>

    @Query("SELECT * FROM Room ORDER BY rname ASC")
    suspend fun getAllRoomList(): List<Room>
}