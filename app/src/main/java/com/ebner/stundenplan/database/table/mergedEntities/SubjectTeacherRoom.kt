package com.ebner.stundenplan.database.table.mergedEntities

import androidx.room.Embedded
import androidx.room.Relation
import com.ebner.stundenplan.database.table.room.Room
import com.ebner.stundenplan.database.table.subject.Subject
import com.ebner.stundenplan.database.table.teacher.Teacher

/**
 * Created by raphael on 21.05.2020.
 * Stundenplan Created in com.ebner.stundenplan.database.table.relations
 */

/**
 * This class captures the relationship between a [Subject] and [Teacher], [Room]
 */
data class SubjectTeacherRoom(

        @Embedded
        val teacher: Teacher,

        @Embedded
        val room: Room,

        @Embedded
        val subject: Subject,

        @Relation(
                entity = Subject::class,
                parentColumn = "tid",
                entityColumn = "s_tid"
        )
        val teachers: List<Subject> = emptyList(),

        @Relation(
                entity = Subject::class,
                parentColumn = "rid",
                entityColumn = "s_rid"
        )
        val rooms: List<Subject> = emptyList()

)