package com.ebner.stundenplan.database.main

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ebner.stundenplan.database.table.examtype.Examtype
import com.ebner.stundenplan.database.table.examtype.ExamtypeDao
import com.ebner.stundenplan.database.table.room.RoomDao
import com.ebner.stundenplan.database.table.settings.Settings
import com.ebner.stundenplan.database.table.settings.SettingsDao
import com.ebner.stundenplan.database.table.subject.Subject
import com.ebner.stundenplan.database.table.subject.SubjectDao
import com.ebner.stundenplan.database.table.teacher.Teacher
import com.ebner.stundenplan.database.table.teacher.TeacherDao
import com.ebner.stundenplan.database.table.year.Year
import com.ebner.stundenplan.database.table.year.YearDao
import java.util.concurrent.Executors

/**
 * Created by raphael on 17.05.2020.
 * Stundenplan Created in com.ebner.stundenplan.database.main
 */

@Database(entities = arrayOf(
        com.ebner.stundenplan.database.table.room.Room::class,
        Teacher::class,
        Subject::class,
        Year::class,
        Examtype::class,
        Settings::class
//DO NOT FORGET TO INCREASE THE VERSION NUMBER
), version = 1, exportSchema = false)
abstract class StundenplanDatabase : RoomDatabase() {

    //Define each Tables DAO (Data Access Object)
    abstract fun roomDao(): RoomDao
    abstract fun teacherDao(): TeacherDao
    abstract fun subjectDao(): SubjectDao
    abstract fun yearDao(): YearDao
    abstract fun examtypeDao(): ExamtypeDao
    abstract fun settingsDao(): SettingsDao


    companion object {
        @Volatile
        private var INSTANCE: StundenplanDatabase? = null

        /*---------------------Create one (only one) instance of the Database--------------------------*/

        fun getInstance(context: Context): StundenplanDatabase =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
                }

        private fun buildDatabase(context: Context) =
                Room.databaseBuilder(context.applicationContext,
                        StundenplanDatabase::class.java, "stundenplandb")
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
                                    getInstance(context).teacherDao().insert(Teacher("Ebner1"))
                                    getInstance(context).teacherDao().insert(Teacher("Ebner2"))
                                    getInstance(context).subjectDao().insert(Subject("test1_1_1", "1_1_1", -10522569, "testnote", false, 1, 1))
                                    getInstance(context).subjectDao().insert(Subject("test1_1_2", "1_1_2", -7504187, "testnote", false, 1, 2))
                                    getInstance(context).subjectDao().insert(Subject("test1_2_1", "1_2_1", -182399, "testnote", false, 2, 1))
                                    getInstance(context).subjectDao().insert(Subject("test1_2_2", "1_2_2", -11473188, "testnote", true, 2, 2))
                                    getInstance(context).yearDao().insert(Year("2019"))
                                    getInstance(context).yearDao().insert(Year("2020"))
                                    getInstance(context).examtypeDao().insert(Examtype("Schulaufgabe", 2.0))
                                    getInstance(context).examtypeDao().insert(Examtype("Ex", 1.0))
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

