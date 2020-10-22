@file:Suppress("unused")

package com.ebner.stundenplan.fragments.settings

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.preference.*
import com.ebner.roomdatabasebackup.core.RoomBackup
import com.ebner.stundenplan.MainActivity
import com.ebner.stundenplan.R
import com.ebner.stundenplan.database.main.StundenplanDatabase
import com.ebner.stundenplan.database.table.exam.Exam
import com.ebner.stundenplan.database.table.exam.ExamViewModel
import com.ebner.stundenplan.database.table.examtype.Examtype
import com.ebner.stundenplan.database.table.examtype.ExamtypeViewModel
import com.ebner.stundenplan.database.table.lesson.Lesson
import com.ebner.stundenplan.database.table.lesson.LessonViewModel
import com.ebner.stundenplan.database.table.room.RoomViewModel
import com.ebner.stundenplan.database.table.schoolLesson.SchoolLesson
import com.ebner.stundenplan.database.table.schoolLesson.SchoolLessonViewModel
import com.ebner.stundenplan.database.table.subject.Subject
import com.ebner.stundenplan.database.table.subject.SubjectViewModel
import com.ebner.stundenplan.database.table.task.Task
import com.ebner.stundenplan.database.table.task.TaskViewModel
import com.ebner.stundenplan.database.table.teacher.Teacher
import com.ebner.stundenplan.database.table.teacher.TeacherViewModel
import com.ebner.stundenplan.database.table.year.Year
import com.ebner.stundenplan.database.table.year.YearViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.nio.file.Files

private const val TITLE_TAG = "settingsActivityTitle"
private lateinit var sharedPreferences: SharedPreferences
private lateinit var pbSettings: ProgressBar

class SettingsActivity : AppCompatActivity(), PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    companion object {
        const val SHARED_PREFS = "sharedPrefs"
        const val BACKUP_AUTOBACKUP = "backupautobackup"
        const val TIMETABLESETTIGNS_ABCYCLE = "timetablesettingsabcycle"
        const val TIMETABLESETTIGNS_ACYCLE = "timetablesettingsacycle" //False = "gerade" / True = "ungerade" Woche
        const val PERSONALIZE_DARKTHEME_VALUE = "personalizedarkthememode" // AppCompatDelegate.MODE_NIGHT_***
        private const val TAG = "debug_SettingsActivity"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        title = getString(R.string.fragment_settings)

        //Initialize SharedPrefs
        sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
        pbSettings = findViewById(R.id.pb_settings)

        if (savedInstanceState == null) {
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.settings, RootFragment())
                    .commit()
        } else {
            title = savedInstanceState.getCharSequence(TITLE_TAG)
        }
        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0) {
                title = getString(R.string.fragment_settings)
            }
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save current activity title so we can set it again after a configuration change
        outState.putCharSequence(TITLE_TAG, title)
    }

    override fun onSupportNavigateUp(): Boolean {

        if (supportFragmentManager.popBackStackImmediate()) {
            return true
        }
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        this.finish()
        return super.onSupportNavigateUp()

    }

    override fun onBackPressed() {
        if (supportFragmentManager.popBackStackImmediate()) {
            return
        } else {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            this.finish()
        }
    }

    override fun onPreferenceStartFragment(caller: PreferenceFragmentCompat, pref: Preference): Boolean {
        // Instantiate the new Fragment
        val fragment = supportFragmentManager.fragmentFactory.instantiate(classLoader, pref.fragment)
        // Replace the existing Fragment with the new Fragment
        supportFragmentManager.beginTransaction()
                .replace(R.id.settings, fragment)
                .addToBackStack(null)
                .commit()
        title = pref.title
        return true
    }


    class RootFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences_root, rootKey)

            val pClearDb: Preference = findPreference(getString(R.string.p_root_clear_database))!!
            val pSampleData: Preference = findPreference(getString(R.string.p_root_sample_data))!!

            pClearDb.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                Toast.makeText(requireContext(), "ClearDB", Toast.LENGTH_SHORT).show()

                MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Achtung!")
                        .setMessage("Es wird die ganze Datenbank gelöscht!! Bitte erstelle zur sicherheit ein Backup")
                        .setPositiveButton("Löschen") { _, _ ->
                            StundenplanDatabase.getInstance(requireContext()).close()
                            Files.delete(requireContext().getDatabasePath(StundenplanDatabase.getInstance(requireContext()).openHelper.databaseName).toPath())
                            val intent = Intent(requireContext(), MainActivity::class.java)
                            startActivity(intent)
                            activity?.finish()
                        }
                        .setNegativeButton("Abbrechen", null)
                        .show()



                true
            }


            pSampleData.onPreferenceClickListener = Preference.OnPreferenceClickListener {

                val roomViewModel = ViewModelProvider(this).get(RoomViewModel::class.java)
                val teacherViewModel = ViewModelProvider(this).get(TeacherViewModel::class.java)
                val subjectViewModel = ViewModelProvider(this).get(SubjectViewModel::class.java)
                val examtypeViewModel = ViewModelProvider(this).get(ExamtypeViewModel::class.java)
                val examViewModel = ViewModelProvider(this).get(ExamViewModel::class.java)
                val lessonViewModel = ViewModelProvider(this).get(LessonViewModel::class.java)
                val schoolLessonViewModel = ViewModelProvider(this).get(SchoolLessonViewModel::class.java)
                val taskViewModel = ViewModelProvider(this).get(TaskViewModel::class.java)
                val yearViewModel = ViewModelProvider(this).get(YearViewModel::class.java)

                MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Achtung!")
                        .setMessage("Es werden Beispieldaten zu deinem Aktuellen Datenbestand hinzugefügt. Die Beispieldaten werden so oft eingefügt, wie du auf den Button drückst (Nicht Empfohlen!). Erstelle vorsichtshalber davor ein Backup, ")
                        .setPositiveButton("Einfügen") { _, _ ->

                            yearViewModel.insert(Year("2021"))
                            roomViewModel.insert(com.ebner.stundenplan.database.table.room.Room("123"))
                            roomViewModel.insert(com.ebner.stundenplan.database.table.room.Room("456"))
                            roomViewModel.insert(com.ebner.stundenplan.database.table.room.Room("789"))
                            roomViewModel.insert(com.ebner.stundenplan.database.table.room.Room("147"))
                            roomViewModel.insert(com.ebner.stundenplan.database.table.room.Room("Sporthalle"))
                            roomViewModel.insert(com.ebner.stundenplan.database.table.room.Room("348"))
                            teacherViewModel.insert(Teacher("Huaba", 0))
                            teacherViewModel.insert(Teacher("Sepp", 0))
                            teacherViewModel.insert(Teacher("Hans", 0))
                            teacherViewModel.insert(Teacher("Johanna", 1))
                            subjectViewModel.insert(Subject("Mathe", "M", -1052396, "", false, 1, 1))
                            subjectViewModel.insert(Subject("Deutsch", "D", -12833281, "Deutschnotiz", false, 2, 2))
                            subjectViewModel.insert(Subject("Englisch", "E", -62976, "", false, 3, 3))
                            subjectViewModel.insert(Subject("Religion", "Reli", -176385, "", false, 1, 4))
                            subjectViewModel.insert(Subject("Sport", "Sp", -11473188, "Sportnotiz", false, 2, 5))
                            subjectViewModel.insert(Subject("Sozialkunde", "SK", -11095553, "", false, 3, 6))
                            subjectViewModel.insert(Subject("Testfach", "T", -82976, "", false, 3, 6))
                            examtypeViewModel.insert(Examtype("Schulaufgabe", 2.0))
                            examtypeViewModel.insert(Examtype("Ex", 1.0))
                            examtypeViewModel.insert(Examtype("Referat", 1.0))
                            examViewModel.insert(Exam(1, 1, 1, 1, 2020, 0, 7))
                            examViewModel.insert(Exam(2, 2, 1, -1, 2020, 1, 8))
                            examViewModel.insert(Exam(3, 3, 1, 2, 2020, 2, 9))
                            examViewModel.insert(Exam(4, 1, 1, -1, 2020, 3, 17))
                            examViewModel.insert(Exam(5, 2, 1, 3, 2020, 4, 27))
                            examViewModel.insert(Exam(6, 3, 1, -1, 2020, 5, 12))
                            examViewModel.insert(Exam(1, 1, 1, 4, 2020, 6, 14))
                            examViewModel.insert(Exam(2, 2, 1, 5, 2020, 7, 15))
                            examViewModel.insert(Exam(3, 3, 1, 6, 2020, 8, 19))
                            examViewModel.insert(Exam(4, 1, 1, 4, 2020, 9, 25))
                            examViewModel.insert(Exam(5, 2, 1, 2, 2020, 10, 24))
                            examViewModel.insert(Exam(6, 3, 1, -1, 2020, 11, 21))
                            schoolLessonViewModel.insert(SchoolLesson(1, 8, 15, 9, 0))
                            schoolLessonViewModel.insert(SchoolLesson(2, 9, 0, 9, 45))
                            schoolLessonViewModel.insert(SchoolLesson(3, 9, 45, 10, 30))
                            schoolLessonViewModel.insert(SchoolLesson(4, 10, 30, 11, 15))
                            schoolLessonViewModel.insert(SchoolLesson(5, 11, 15, 12, 0))
                            lessonViewModel.insert(Lesson(1, -1, 1, 1, 1))
                            lessonViewModel.insert(Lesson(2, -1, 1, 2, 1))
                            lessonViewModel.insert(Lesson(3, -1, 1, 3, 1))
                            lessonViewModel.insert(Lesson(4, -1, 1, 4, 1))
                            lessonViewModel.insert(Lesson(5, -1, 1, 5, 1))
                            lessonViewModel.insert(Lesson(1, -1, 2, 6, 1))
                            lessonViewModel.insert(Lesson(2, -1, 2, 2, 1))
                            lessonViewModel.insert(Lesson(3, -1, 2, 1, 1))
                            lessonViewModel.insert(Lesson(4, -1, 2, 2, 1))
                            lessonViewModel.insert(Lesson(5, -1, 2, 3, 1))
                            lessonViewModel.insert(Lesson(1, -1, 3, 4, 1))
                            lessonViewModel.insert(Lesson(2, -1, 3, 4, 1))
                            lessonViewModel.insert(Lesson(3, -1, 3, 6, 1))
                            lessonViewModel.insert(Lesson(4, -1, 3, 7, 1))
                            lessonViewModel.insert(Lesson(5, -1, 3, 1, 1))
                            lessonViewModel.insert(Lesson(1, -1, 4, 2, 1))
                            lessonViewModel.insert(Lesson(2, -1, 4, 3, 1))
                            lessonViewModel.insert(Lesson(3, -1, 4, 4, 1))
                            lessonViewModel.insert(Lesson(4, -1, 4, 5, 1))
                            lessonViewModel.insert(Lesson(5, -1, 4, 6, 1))
                            lessonViewModel.insert(Lesson(1, -1, 5, 2, 1))
                            lessonViewModel.insert(Lesson(2, -1, 5, 7, 1))
                            lessonViewModel.insert(Lesson(3, -1, 5, 4, 1))
                            lessonViewModel.insert(Lesson(4, -1, 5, 6, 1))
                            taskViewModel.insert(Task("Aufgabe1", "niceNotiz1", 1, 10, 2020, false, 1, 1))
                            taskViewModel.insert(Task("Aufgabe2", "niceNotiz2", 2, 10, 2020, false, 2, 1))
                            taskViewModel.insert(Task("Aufgabe3", "niceNotiz3", 3, 10, 2020, false, 3, 1))
                            taskViewModel.insert(Task("Aufgabe4", "", 4, 10, 2020, false, 4, 1))
                            taskViewModel.insert(Task("Aufgabe5", "", 5, 10, 2020, false, 5, 1))
                            taskViewModel.insert(Task("Aufgabe6", "", 6, 10, 2020, false, 6, 1))
                            taskViewModel.insert(Task("Aufgabe7", "niceNotiz2", 15, 10, 2020, true, 2, 1))
                            taskViewModel.insert(Task("Aufgabe8", "niceNotiz3", 16, 10, 2020, true, 3, 1))
                            taskViewModel.insert(Task("Aufgabe9", "", 17, 10, 2020, true, 4, 1))
                            taskViewModel.insert(Task("Aufgabe10", "", 18, 10, 2020, true, 5, 1))
                            taskViewModel.insert(Task("Aufgabe11", "", 19, 10, 2020, true, 6, 1))
                            taskViewModel.insert(Task("Aufgabe12", "", 20, 10, 2020, true, 6, 1))


                        }
                        .setNegativeButton("Abbrechen", null)
                        .show()


                true
            }
        }


    }

    class TimetableSettingsFragment : PreferenceFragmentCompat() {

        private lateinit var cbpAbCycle: CheckBoxPreference
        private lateinit var spAcycle: SwitchPreference

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences_timetablesettings, rootKey)

            cbpAbCycle = findPreference(getString(R.string.cbp_timetablesettings_abcycle))!!
            spAcycle = findPreference(getString(R.string.sp_timetablesettings_acycle))!!

            loadApplyPreference()

            cbpAbCycle.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
                val editor = sharedPreferences.edit()
                editor.putBoolean(TIMETABLESETTIGNS_ABCYCLE, newValue.toString().toBoolean())
                editor.apply()

                //true to update the view
                true
            }

            spAcycle.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
                val editor = sharedPreferences.edit()
                editor.putBoolean(TIMETABLESETTIGNS_ACYCLE, newValue.toString().toBoolean())
                editor.apply()

                //true to update the view
                true
            }

        }

        private fun loadApplyPreference() {
            cbpAbCycle.isChecked = sharedPreferences.getBoolean(TIMETABLESETTIGNS_ABCYCLE, false)
            spAcycle.isChecked = sharedPreferences.getBoolean(TIMETABLESETTIGNS_ACYCLE, false)

        }

    }

    class PersonalizeFragment : PreferenceFragmentCompat() {
        private lateinit var lpDesign: ListPreference


        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences_personalize, rootKey)
            lpDesign = findPreference(getString(R.string.lp_personalize_design))!!

            lpDesign.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
                val editor = sharedPreferences.edit()
                editor.putInt(PERSONALIZE_DARKTHEME_VALUE, newValue.toString().toInt())
                editor.apply()

                when (newValue.toString().toInt()) {
                    1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    3 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }

                //true to update the view
                true
            }

        }

        private fun loadApplyPreference() {
        }

    }

    class BackupFragment : PreferenceFragmentCompat() {

        private lateinit var pManBackup: Preference
        private lateinit var pManBackupExport: Preference
        private lateinit var pAutoBackup: SwitchPreference
        private lateinit var pRestore: Preference
        private lateinit var pImportRestore: Preference

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences_backup, rootKey)

            /*---------------------Link items to Layout--------------------------*/
            pManBackup = findPreference(getString(R.string.p_backup_manBackup))!!
            pManBackupExport = findPreference(getString(R.string.p_backup_manBackupExport))!!
            pAutoBackup = findPreference(getString(R.string.spc_backup_autoBackup))!!
            pRestore = findPreference(getString(R.string.p_back_restore))!!
            pImportRestore = findPreference(getString(R.string.p_backup_importRestore))!!

            loadApplySP()

            pAutoBackup.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->

                val editor = sharedPreferences.edit()
                editor.putBoolean(BACKUP_AUTOBACKUP, newValue.toString().toBoolean())
                editor.apply()

                //true to update the view
                true
            }

            pManBackup.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                pbSettings.visibility = View.VISIBLE
                RoomBackup()
                        .context(requireContext())
                        .database(StundenplanDatabase.getInstance(requireContext()))
                        .backupIsEncrypted(true)
                        .maxFileCount(15)
                        .onCompleteListener { success, _ ->
                            if (success) {
                                val intent = Intent(requireContext(), MainActivity::class.java)
                                startActivity(intent)
                                Toast.makeText(requireContext(), "Backup successful", Toast.LENGTH_SHORT).show()
                                activity?.finish()
                            } else {
                                Toast.makeText(requireContext(), "Backup failed", Toast.LENGTH_SHORT).show()
                                pbSettings.visibility = View.INVISIBLE
                            }
                        }
                        .backup()

                true
            }

            pManBackupExport.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                Toast.makeText(requireContext(), "comming soon", Toast.LENGTH_SHORT).show()
                true
            }

            pRestore.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                pbSettings.visibility = View.VISIBLE
                RoomBackup()
                        .context(requireContext())
                        .database(StundenplanDatabase.getInstance(requireContext()))
                        .backupIsEncrypted(true)
                        .onCompleteListener { success, _ ->
                            if (success) {
                                val intent = Intent(requireContext(), MainActivity::class.java)
                                startActivity(intent)
                                Toast.makeText(requireContext(), "Restore successful", Toast.LENGTH_SHORT).show()
                                activity?.finish()
                            } else {
                                Toast.makeText(requireContext(), "Restore failed", Toast.LENGTH_SHORT).show()
                                pbSettings.visibility = View.INVISIBLE
                            }
                        }
                        .restore()


                true
            }

            pImportRestore.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                Toast.makeText(requireContext(), "comming soon", Toast.LENGTH_SHORT).show()

                true
            }

        }

        private fun loadApplySP() {
            pAutoBackup.isChecked = sharedPreferences.getBoolean(BACKUP_AUTOBACKUP, false)
        }


    }


}