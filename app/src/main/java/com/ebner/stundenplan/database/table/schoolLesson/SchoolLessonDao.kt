package com.ebner.stundenplan.database.table.schoolLesson

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.ebner.stundenplan.database.table.BaseDao

/**
 * Created by raphael on 07.06.2020.
 * Stundenplan Created in com.ebner.stundenplan.database.table.schoolLesson
 */
@Dao
interface SchoolLessonDao : BaseDao<SchoolLesson> {

    @Query("SELECT * FROM schoollesson ORDER BY slnumber ASC")
    fun getAllSchoolLesson(): LiveData<List<SchoolLesson>>

}