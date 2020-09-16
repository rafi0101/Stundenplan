package com.ebner.stundenplan.database.main

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ebner.stundenplan.database.table.exam.Exam
import com.ebner.stundenplan.database.table.exam.ExamDao
import com.ebner.stundenplan.database.table.examtype.Examtype
import com.ebner.stundenplan.database.table.examtype.ExamtypeDao
import com.ebner.stundenplan.database.table.lesson.Lesson
import com.ebner.stundenplan.database.table.lesson.LessonDao
import com.ebner.stundenplan.database.table.room.RoomDao
import com.ebner.stundenplan.database.table.schoolLesson.SchoolLesson
import com.ebner.stundenplan.database.table.schoolLesson.SchoolLessonDao
import com.ebner.stundenplan.database.table.settings.Settings
import com.ebner.stundenplan.database.table.settings.SettingsDao
import com.ebner.stundenplan.database.table.subject.Subject
import com.ebner.stundenplan.database.table.subject.SubjectDao
import com.ebner.stundenplan.database.table.task.Task
import com.ebner.stundenplan.database.table.task.TaskDao
import com.ebner.stundenplan.database.table.teacher.Teacher
import com.ebner.stundenplan.database.table.teacher.TeacherDao
import com.ebner.stundenplan.database.table.year.Year
import com.ebner.stundenplan.database.table.year.YearDao
import java.util.concurrent.Executors

/**
 * Created by raphael on 17.05.2020.
 * Stundenplan Created in com.ebner.stundenplan.database.main
 */

@Database(entities = [
    com.ebner.stundenplan.database.table.room.Room::class,
    Teacher::class,
    Subject::class,
    Year::class, Examtype::class,
    Settings::class,
    Exam::class,
    Lesson::class,
    SchoolLesson::class,
    Task::class
], version = 1, exportSchema = false)
abstract class StundenplanDatabase : RoomDatabase() {

    //Define each Tables DAO (Data Access Object)
    abstract fun roomDao(): RoomDao
    abstract fun teacherDao(): TeacherDao
    abstract fun subjectDao(): SubjectDao
    abstract fun yearDao(): YearDao
    abstract fun examtypeDao(): ExamtypeDao
    abstract fun settingsDao(): SettingsDao
    abstract fun examDao(): ExamDao
    abstract fun lessonDao(): LessonDao
    abstract fun schoolLessonDao(): SchoolLessonDao
    abstract fun taskDao(): TaskDao


    companion object {

        const val DATABASE_NAME = "stundenplandb"

        @Volatile
        private var INSTANCE: StundenplanDatabase? = null

        /*---------------------Create one (only one) instance of the Database--------------------------*/

        fun getInstance(context: Context): StundenplanDatabase =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
                }

        private fun buildDatabase(context: Context) =
                Room.databaseBuilder(context.applicationContext,
                        StundenplanDatabase::class.java, DATABASE_NAME)
                        //Delete Database, when something changed
                        .fallbackToDestructiveMigration()
                        // prepopulate the database after onCreate was called
                        .addCallback(object : Callback() {
                            override fun onCreate(db: SupportSQLiteDatabase) {
                                super.onCreate(db)
                                // insert the data on the IO Thread
                                ioThread {
                                    getInstance(context).roomDao().insert(com.ebner.stundenplan.database.table.room.Room("123"))
                                    getInstance(context).roomDao().insert(com.ebner.stundenplan.database.table.room.Room("456"))
                                    getInstance(context).roomDao().insert(com.ebner.stundenplan.database.table.room.Room("789"))
                                    getInstance(context).roomDao().insert(com.ebner.stundenplan.database.table.room.Room("147"))
                                    getInstance(context).roomDao().insert(com.ebner.stundenplan.database.table.room.Room("Sporthalle"))
                                    getInstance(context).roomDao().insert(com.ebner.stundenplan.database.table.room.Room("348"))
                                    getInstance(context).teacherDao().insert(Teacher("Huaba"))
                                    getInstance(context).teacherDao().insert(Teacher("Sepp"))
                                    getInstance(context).teacherDao().insert(Teacher("Bäda"))
                                    getInstance(context).subjectDao().insert(Subject("Mathe", "M", -1052396, "", false, 1, 1))
                                    getInstance(context).subjectDao().insert(Subject("Deutsch", "D", -12833281, "Deutschnotiz", false, 2, 2))
                                    getInstance(context).subjectDao().insert(Subject("Englisch", "E", -62976, "", true, 3, 3))
                                    getInstance(context).subjectDao().insert(Subject("Religion", "Reli", -176385, "", true, 1, 4))
                                    getInstance(context).subjectDao().insert(Subject("Sport", "Sp", -11473188, "Sportnotiz", false, 2, 5))
                                    getInstance(context).subjectDao().insert(Subject("Sozialkunde", "SK", -11095553, "", false, 3, 6))
                                    //Is needed for productive
                                    getInstance(context).yearDao().insert(Year("10A"))
                                    getInstance(context).yearDao().insert(Year("9A"))
                                    getInstance(context).examtypeDao().insert(Examtype("Schulaufgabe", 2.0))
                                    getInstance(context).examtypeDao().insert(Examtype("Ex", 1.0))
                                    getInstance(context).examtypeDao().insert(Examtype("Referat", 1.0))
                                    //Is needed for productive
                                    getInstance(context).settingsDao().insert(Settings(1))
                                    getInstance(context).examDao().insert(Exam(1, 1, 1, 1, 2020, 1, 7))
                                    getInstance(context).examDao().insert(Exam(2, 2, 1, -1, 2020, 2, 8))
                                    getInstance(context).examDao().insert(Exam(3, 3, 1, 2, 2020, 3, 9))
                                    getInstance(context).examDao().insert(Exam(4, 1, 1, -1, 2020, 4, 17))
                                    getInstance(context).examDao().insert(Exam(5, 2, 1, 3, 2020, 5, 27))
                                    getInstance(context).examDao().insert(Exam(6, 3, 1, -1, 2020, 6, 12))
                                    getInstance(context).examDao().insert(Exam(1, 1, 1, 4, 2020, 7, 14))
                                    getInstance(context).examDao().insert(Exam(2, 2, 1, 5, 2020, 8, 15))
                                    getInstance(context).examDao().insert(Exam(3, 3, 1, 6, 2020, 9, 19))
                                    getInstance(context).examDao().insert(Exam(4, 1, 1, 4, 2020, 10, 25))
                                    getInstance(context).examDao().insert(Exam(5, 2, 2, 2, 2020, 11, 24))
                                    getInstance(context).examDao().insert(Exam(6, 3, 2, -1, 2020, 12, 21))
                                    getInstance(context).schoolLessonDao().insert(SchoolLesson(1, 8, 15, 9, 0))
                                    getInstance(context).schoolLessonDao().insert(SchoolLesson(2, 9, 0, 9, 45))
                                    getInstance(context).schoolLessonDao().insert(SchoolLesson(3, 9, 45, 10, 30))
                                    getInstance(context).lessonDao().insert(Lesson(1, 1, 1, 1))
                                    getInstance(context).lessonDao().insert(Lesson(1, 2, 4, 1))
                                    getInstance(context).lessonDao().insert(Lesson(1, 3, 5, 1))
                                    getInstance(context).lessonDao().insert(Lesson(2, 1, 6, 1))
                                    getInstance(context).lessonDao().insert(Lesson(2, 2, 2, 1))
                                    getInstance(context).lessonDao().insert(Lesson(3, 3, 3, 1))
                                    getInstance(context).taskDao().insert(Task("Aufgabe1", "niceNotiz1", 1, 10, 2020, 1, 1))
                                    getInstance(context).taskDao().insert(Task("Aufgabe2", "niceNotiz2", 1, 10, 2020, 2, 1))
                                    getInstance(context).taskDao().insert(Task("Aufgabe3", "niceNotiz3", 1, 10, 2020, 3, 1))
                                    getInstance(context).taskDao().insert(Task("Aufgabe4", "", 1, 10, 2020, 4, 1))
                                    getInstance(context).taskDao().insert(Task("Aufgabe5", "", 1, 10, 2020, 5, 1))
                                    getInstance(context).taskDao().insert(Task("Aufgabe6", "", 1, 10, 2020, 6, 1))


                                }
                            }
                        })
                        .build()

        /*---------------------This runs a background task--------------------------*/
        private val IO_EXECUTOR = Executors.newSingleThreadExecutor()

        /**
         * Utility method to run blocks on a dedicated background thread, used for io/database work.
         */
        fun ioThread(f: () -> Unit) {
            IO_EXECUTOR.execute(f)
        }

    }


}

