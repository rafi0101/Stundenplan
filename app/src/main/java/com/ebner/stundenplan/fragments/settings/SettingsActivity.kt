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
import androidx.preference.*
import com.ebner.roomdatabasebackup.core.RoomBackup
import com.ebner.stundenplan.MainActivity
import com.ebner.stundenplan.R
import com.ebner.stundenplan.database.main.StundenplanDatabase

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
                title = "SettingsActivity"
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
        private lateinit var pAutoBackup: SwitchPreference
        private lateinit var pRestore: Preference

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences_backup, rootKey)

            /*---------------------Link items to Layout--------------------------*/
            pManBackup = findPreference(getString(R.string.p_backup_manBackup))!!
            pAutoBackup = findPreference(getString(R.string.spc_backup_autoBackup))!!
            pRestore = findPreference(getString(R.string.p_back_restore))!!

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


        }

        private fun loadApplySP() {
            pAutoBackup.isChecked = sharedPreferences.getBoolean(BACKUP_AUTOBACKUP, false)
        }


    }


}