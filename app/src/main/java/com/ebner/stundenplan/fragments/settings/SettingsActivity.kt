package com.ebner.stundenplan.fragments.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.ebner.stundenplan.R

private const val TITLE_TAG = "settingsActivityTitle"
private lateinit var sharedPreferences: SharedPreferences

private val TAG = "debug_SettingsActivity"

class SettingsActivity : AppCompatActivity(), PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {


    companion object {
        private const val SHARED_PREFS = "sharedPrefs"
        private const val BACKUP_AUTOBACKUP = "backupautobackup"

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        //Initialize SharedPrefs
        sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)

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
        super.onBackPressed()
        return super.onSupportNavigateUp()

    }

    override fun onPreferenceStartFragment(caller: PreferenceFragmentCompat, pref: Preference): Boolean {
        // Instantiate the new Fragment
        val args = pref.extras
        val fragment = supportFragmentManager.fragmentFactory.instantiate(
                classLoader,
                pref.fragment
        ).apply {
            arguments = args
            setTargetFragment(caller, 0)
        }
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

    class GenearlFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences_general, rootKey)
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


        }

        private fun loadApplySP() {
            pAutoBackup.isChecked = sharedPreferences.getBoolean(BACKUP_AUTOBACKUP, false)
        }


    }


}