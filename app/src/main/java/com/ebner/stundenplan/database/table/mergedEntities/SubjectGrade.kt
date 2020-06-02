package com.ebner.stundenplan.database.table.mergedEntities

import androidx.room.Embedded
import com.ebner.stundenplan.database.table.subject.Subject

/**
 * Created by raphael on 31.05.2020.
 * Stundenplan Created in com.ebner.stundenplan.customAdapter
 */
/**
 * This class captures the relationship between a [SubjectTeacherRoom] and the grade
 */
class SubjectGrade(

        @Embedded
        val subject: Subject,

        val grade: Double
)