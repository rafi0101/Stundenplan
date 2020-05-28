package com.ebner.stundenplan.database.table.room

import androidx.lifecycle.LiveData

/**
 * Created by raphael on 17.05.2020.
 * Stundenplan Created in com.ebner.stundenplan.database.table.room
 */

class RoomRepository(
        private val roomDao: RoomDao
) {

    /*---------------------Just pass the queries to the DAO--------------------------*/
    fun insertRoom(room: Room) = roomDao.insert(room)
    fun updateRoom(room: Room) = roomDao.update(room)
    fun deleteRoom(room: Room) = roomDao.delete(room)

    //Live data view
    val getAllRoom: LiveData<List<Room>> = roomDao.getAllRoom()

    suspend fun getAllRoomList(): List<Room> = roomDao.getAllRoomList()


}