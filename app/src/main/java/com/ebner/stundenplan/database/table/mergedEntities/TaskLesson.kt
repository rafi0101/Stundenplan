package com.ebner.stundenplan.database.table.mergedEntities

import androidx.room.Embedded
import com.ebner.stundenplan.database.table.task.Task

/**
 * Created by raphael on 11.07.2020.
 * Stundenplan Created in com.ebner.stundenplan.database.table.task
 */
/**
 * This class captures the relationship between a [Task] and [LessonSubjectSchoollessonYear]
 */
class TaskLesson(
        @Embedded
        val task: Task,

        @Embedded
        val lessonSubjectSchoollessonYear: LessonSubjectSchoollessonYear

)




