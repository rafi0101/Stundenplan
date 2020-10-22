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
    Year::class,
    Examtype::class,
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

        private const val DATABASE_NAME = "stundenplandb"

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
                                    //Is needed for productive
                                    getInstance(context).yearDao().insert(Year("2020"))
                                    //Is needed for productive
                                    getInstance(context).settingsDao().insert(Settings(1))

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

