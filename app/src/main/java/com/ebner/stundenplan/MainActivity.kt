package com.ebner.stundenplan

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
import com.ebner.stundenplan.fragments.settings.FragmentSettings
import com.google.android.material.navigation.NavigationView
import com.mikepenz.aboutlibraries.LibsBuilder

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    private lateinit var toolbar: Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        changeFragment(FragmentHome())

        /*---------------------Create the Database, if not already exist--------------------------*/
        val subjectViewModel = ViewModelProvider(this).get(SubjectViewModel::class.java)
        subjectViewModel.allSubject.observe(this, Observer {
            //Nothing to do here, because just for initializing
        })
    }

    private fun changeFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment, fragment)
        //transaction.addToBackStack(null); //need when you can press back and something should happen (go to last fragment)
        transaction.commit()
    }

    /*---------------------TO DO when item in navigation drawer pressed--------------------------*/
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> changeFragment(FragmentHome())
            R.id.nav_timetable -> changeFragment(FragmentTimetable())
            R.id.nav_exam -> changeFragment(FragmentExam())
            R.id.nav_task -> changeFragment(FragmentTask())
            R.id.nav_subject -> changeFragment(FragmentSubject())
            R.id.nav_teacher -> changeFragment(FragmentTeacher())
            R.id.nav_room -> changeFragment(FragmentRoom())
            R.id.nav_year -> changeFragment(FragmentYear())
            R.id.nav_examtype -> changeFragment(FragmentExamtype())
            R.id.nav_schoollesson -> changeFragment(FragmentSchoolLesson())
            R.id.nav_settings -> changeFragment(FragmentSettings())
            R.id.nav_info -> {
                val fragment = LibsBuilder()
                        .withFields(R.string::class.java.fields) // in some cases it may be needed to provide the R class, if it can not be automatically resolved
                        .withAboutDescription("for more information check out my github project\nCreated by Raphael Ebner")
                        .withActivityTitle(getString(R.string.app_name))
                        .supportFragment()
                changeFragment(fragment)
            }
            else -> {
            }
        }
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
                changeFragment(FragmentHome())
                navigationView.setCheckedItem(R.id.nav_home)
            }
        }

    }
}