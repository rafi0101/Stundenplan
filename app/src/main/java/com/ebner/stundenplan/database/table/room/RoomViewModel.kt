package com.ebner.stundenplan.database.table.room

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.ebner.stundenplan.database.main.StundenplanDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by raphael on 17.05.2020.
 * Stundenplan Created in com.ebner.stundenplan.database.table.room
 */

class RoomViewModel(application: Application) : AndroidViewModel(application) {

    private val roomRepository: RoomRepository

/*     Using LiveData and caching what getAlphabetizedWords returns has several benefits:
     - We can put an observer on the data (instead of polling for changes) and only update the
       the UI when the data actually changes.
     - Repository is completely separated from the UI through the ViewModel.*/


    //This Class is called, when you want to do something from your Activity / Fragment


    val allRoom: LiveData<List<Room>>

    /*---------------------Define the Database, and the Repository--------------------------*/
    init {
        val roomDao = StundenplanDatabase.getInstance(application).roomDao()
        roomRepository = RoomRepository(roomDao)
        allRoom = roomRepository.getAllRoom

    }

    /*---------------------Define default queries--------------------------*/
    fun insert(room: Room) = viewModelScope.launch(Dispatchers.IO) {
        roomRepository.insertRoom(room)
    }

    fun update(room: Room) = viewModelScope.launch(Dispatchers.IO) {
        roomRepository.updateRoom(room)
    }

    fun delete(room: Room) = viewModelScope.launch(Dispatchers.IO) {
        roomRepository.deleteRoom(room)
    }

    suspend fun allRoomList(): List<Room> {
        return roomRepository.getAllRoomList()
    }

}