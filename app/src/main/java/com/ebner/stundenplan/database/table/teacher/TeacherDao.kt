package com.ebner.stundenplan.database.table.teacher

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.ebner.stundenplan.database.table.BaseDao

/**
 * Created by raphael on 15.05.2020.
 * Stundenplan Created in com.ebner.stundenplan.database.table.teacher
 */
/** DAO (Data Access Object)
 * To communicate from Repository to the SQLite Table
 * In there you define each "query" you want to use
 */
@Dao
interface TeacherDao : BaseDao<Teacher?> {

    @Query("SELECT * FROM Teacher ORDER BY tname ASC")
    fun getAllTeacher(): LiveData<List<Teacher>>


    @Query("SELECT * FROM teacher ORDER BY tname ASC")
    suspend fun getAllTeacherList(): List<Teacher>
}