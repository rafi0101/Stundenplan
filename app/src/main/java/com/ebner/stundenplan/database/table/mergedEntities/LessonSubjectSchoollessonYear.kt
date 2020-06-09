package com.ebner.stundenplan.database.table.mergedEntities

import androidx.room.Embedded
import com.ebner.stundenplan.database.table.lesson.Lesson
import com.ebner.stundenplan.database.table.room.Room
import com.ebner.stundenplan.database.table.schoolLesson.SchoolLesson
import com.ebner.stundenplan.database.table.subject.Subject
import com.ebner.stundenplan.database.table.teacher.Teacher
import com.ebner.stundenplan.database.table.year.Year


/**
 * Created by raphael on 07.06.2020.
 * Stundenplan Created in com.ebner.stundenplan.database.table.mergedEntities
 */
/**
 * This class captures the relationship between a [Lesson] and [Year], [Subject], [Room], [Teacher] [SchoolLesson]
 */
class LessonSubjectSchoollessonYear(

        @Embedded
        val lesson: Lesson,

        @Embedded
        val schoolLesson: SchoolLesson,

        @Embedded
        val subject: Subject,

        @Embedded
        val room: Room,

        @Embedded
        val teacher: Teacher,

        @Embedded
        val year: Year

)