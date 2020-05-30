package com.ebner.stundenplan.database.table.mergedEntities

import androidx.room.Embedded
import com.ebner.stundenplan.database.table.exam.Exam
import com.ebner.stundenplan.database.table.examtype.Examtype
import com.ebner.stundenplan.database.table.subject.Subject
import com.ebner.stundenplan.database.table.year.Year

/**
 * Created by raphael on 29.05.2020.
 * Stundenplan Created in com.ebner.stundenplan.database.table.mergedEntities
 */
/**
 * This class captures the relationship between a [Exam] and [Year], [Examtype], [Subject]
 */
class ExamSubjectYearExamtype(

        @Embedded
        val exam: Exam,

        @Embedded
        val subject: Subject,

        @Embedded
        val year: Year,

        @Embedded
        val examtype: Examtype


)