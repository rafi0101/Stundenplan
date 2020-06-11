package com.ebner.stundenplan

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ebner.stundenplan.database.table.subject.SubjectViewModel
import com.ebner.stundenplan.fragments.main.FragmentExam
import com.ebner.stundenplan.fragments.main.FragmentHome
import com.ebner.stundenplan.fragments.main.FragmentTask
import com.ebner.stundenplan.fragments.main.FragmentTimetable
import com.ebner.stundenplan.fragments.manage.*
import com.ebner.stundenplan.fragments.settings.SettingsActivity
import com.google.android.material.navigation.NavigationView
import com.mikepenz.aboutlibraries.LibsBuilder

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object {
        const val EXTRA_FRAGMENT_ID = "com.ebner.stundenplan.EXTRA_FRAGMENT_ID"
    }


    private lateinit var toolbar: Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    private var currentFragment: Int = R.id.nav_home


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(null)
        setContentView(R.layout.activity_main)

        /*---------------------Items--------------------------*/
        toolbar = findViewById(R.id.toolbar)
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)


        /*---------------------Tool Bar--------------------------*/
        setSupportActionBar(toolbar)


        /*---------------------Navigation Drawer Menu--------------------------*/
        val actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        //  drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        navigationView.bringToFront()
        navigationView.setNavigationItemSelectedListener(this)


        /*---------------------Select first Item in NavDrawer--------------------------*/
        val menu = navigationView.menu
        val menuItem = menu.getItem(0)
        menuItem.isChecked = true


        /*---------------------Define Default Fragment--------------------------*/
        if (savedInstanceState != null) {
            changeFragment(savedInstanceState.getInt(EXTRA_FRAGMENT_ID, R.id.nav_home))
        } else {
            changeFragment(R.id.nav_home)
        }
        /*---------------------Create the Database, if not already exist--------------------------*/
        val subjectViewModel = ViewModelProvider(this).get(SubjectViewModel::class.java)
        subjectViewModel.allSubject.observe(this, Observer {
            //Nothing to do here, because just for initializing
        })
    }

    fun changeFragment(id: Int) {

        currentFragment = id

        navigationView.setCheckedItem(currentFragment)

        val newFragment: Fragment

        when (currentFragment) {
            R.id.nav_home -> newFragment = FragmentHome()
            R.id.nav_timetable -> newFragment = FragmentTimetable()
            R.id.nav_exam -> newFragment = FragmentExam()
            R.id.nav_task -> newFragment = FragmentTask()
            R.id.nav_lesson -> newFragment = FragmentLesson()
            R.id.nav_subject -> newFragment = FragmentSubject()
            R.id.nav_teacher -> newFragment = FragmentTeacher()
            R.id.nav_room -> newFragment = FragmentRoom()
            R.id.nav_year -> newFragment = FragmentYear()
            R.id.nav_examtype -> newFragment = FragmentExamtype()
            R.id.nav_schoollesson -> newFragment = FragmentSchoolLesson()
            R.id.nav_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                currentFragment = R.id.nav_home
                navigationView.setCheckedItem(currentFragment)
                newFragment = FragmentHome()
            }
            R.id.nav_info -> {
                val infoFragment = LibsBuilder()
                        .withFields(R.string::class.java.fields) // in some cases it may be needed to provide the R class, if it can not be automatically resolved
                        .withAboutDescription("for more information check out my github project\nCreated by Raphael Ebner")
                        .withActivityTitle(getString(R.string.app_name))
                        .supportFragment()
                newFragment = infoFragment
            }
            else -> {
                return
            }
        }


        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment, newFragment)
        //transaction.addToBackStack(null); //need when you can press back and something should happen (go to last fragment)
        transaction.commit()

    }

    /*---------------------TO DO when item in navigation drawer pressed--------------------------*/
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        changeFragment(item.itemId)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    /*---------------------first close navigation drawer, then on 2 times pressed back, leave app--------------------------*/
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            val currentFragment = supportFragmentManager.fragments.last()
            if (currentFragment is FragmentHome) {
                super.onBackPressed()
            } else {
                changeFragment(R.id.nav_home)
                navigationView.setCheckedItem(R.id.nav_home)
            }
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(EXTRA_FRAGMENT_ID, currentFragment)
    }
}