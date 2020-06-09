package com.ebner.stundenplan.fragments.manage

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.ebner.stundenplan.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class ActivityAddEditExamtype : AppCompatActivity() {

    companion object {
        const val EXTRA_ETID = "com.ebner.stundenplan.fragments.manage.EXTRA_ETID"
        const val EXTRA_ETNAME = "com.ebner.stundenplan.fragments.manage.EXTRA_ETNAME"
        const val EXTRA_ETWEIGHT = "com.ebner.stundenplan.fragments.manage.EXTRA_ETWEIGHT"
    }

    private lateinit var tietEtname: TextInputEditText
    private lateinit var tilEtname: TextInputLayout
    private lateinit var tietEtweight: TextInputEditText
    private lateinit var tilEtweight: TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_examtype)


        /*---------------------Add back Button to the toolbar--------------------------*/
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        /*---------------------Link items to Layout--------------------------*/
        tietEtname = findViewById(R.id.tiet_examtype_etname)
        tilEtname = findViewById(R.id.til_examtype_etname)
        tietEtweight = findViewById(R.id.tiet_examtype_etweight)
        tilEtweight = findViewById(R.id.til_examtype_etweight)


        /*---------------------when calling this Activity, are some extras passed?--------------------------*/
        if (intent.hasExtra(EXTRA_ETID)) {
            title = getString(R.string.fragment_examtype) + " bearbeiten"
            tietEtname.setText(intent.getStringExtra(EXTRA_ETNAME))
            tietEtweight.setText(intent.getDoubleExtra(EXTRA_ETWEIGHT, -1.0).toString())
        } else {
            title = "Neue " + getString(R.string.fragment_examtype)
        }
        //Remove the error message, if user starts typing
        tietEtname.addTextChangedListener {
            tilEtname.error = ""
        }
        tietEtweight.addTextChangedListener {
            tilEtweight.error = ""
        }


    }

    /*---------------------Save current entrys, and return to Fragment--------------------------*/
    private fun saveExamtype() {
        var error = false

        /*---------------------If EditText has invalid value--------------------------*/
        if (TextUtils.isEmpty(tietEtname.text.toString()) || TextUtils.getTrimmedLength(tietEtname.text.toString()) == 0) {
            tilEtname.error = "Gib einen Namen ein!"
            error = true
        }
        if (TextUtils.isEmpty(tietEtweight.text.toString()) || tietEtweight.text.toString().toDoubleOrNull() == null) {
            tilEtweight.error = "Gib eine korrekte Gewichtung ein!"
            error = true
        }
        if (error) return


        val etname = tietEtname.text.toString()
        val etweight = tietEtweight.text.toString().toDouble()


        val data = Intent()
        data.putExtra(EXTRA_ETNAME, etname)
        data.putExtra(EXTRA_ETWEIGHT, etweight)
        val id = intent.getIntExtra(EXTRA_ETID, -1)
        if (id != -1) {
            data.putExtra(EXTRA_ETID, id)
        }
        setResult(Activity.RESULT_OK, data)
        finish()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.save_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            //Save Button
            R.id.nav_save -> {
                saveExamtype()
                true
            }
            //Back Button
            android.R.id.home -> {
                super.onBackPressed()
                true

            }
            else -> super.onOptionsItemSelected(item)
        }
    }


}