package com.ebner.stundenplan.fragments.settings

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.ebner.roomdatabasebackup.core.RoomBackup
import com.ebner.stundenplan.MainActivity
import com.ebner.stundenplan.R
import com.ebner.stundenplan.database.main.StundenplanDatabase

private const val TITLE_TAG = "settingsActivityTitle"
private lateinit var sharedPreferences: SharedPreferences
private lateinit var pbSettings: ProgressBar

private val TAG = "debug_SettingsActivity"

class SettingsActivity : AppCompatActivity(), PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {


    companion object {
        const val SHARED_PREFS = "sharedPrefs"
        const val BACKUP_AUTOBACKUP = "backupautobackup"

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