package com.ebner.stundenplan.fragments.manage

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev.materialspinner.MaterialSpinner
import com.ebner.stundenplan.R
import com.ebner.stundenplan.database.table.settings.Settings
import com.ebner.stundenplan.database.table.settings.SettingsViewModel
import com.ebner.stundenplan.database.table.year.Year
import com.ebner.stundenplan.database.table.year.YearListAdapter
import com.ebner.stundenplan.database.table.year.YearViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import kotlin.math.roundToInt

/**
 * A simple [Fragment] subclass.
 */
class FragmentYear : Fragment(), YearListAdapter.onItemClickListener {

    private val TAG = "debug_FragmentYear"

    private lateinit var yearViewModel: YearViewModel
    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var cl_year: CoordinatorLayout
    private lateinit var sp_activeyear: MaterialSpinner
    private var activeYearID: Int = -1

    companion object {
        private const val ADD_YEAR_REQUEST = 1
        private const val EDIT_YEAR_REQUEST = 2
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_year, container, false)

        activity?.title = getString(R.string.fragment_years)


        /*---------------------Set correct layout margin to main FrameLaout--------------------------*/
        val all: Int = convertDpToPixel(16F, root.context).roundToInt()
        val fragmentmain: FrameLayout? = activity?.findViewById(R.id.fragment)
        val params: ViewGroup.MarginLayoutParams = fragmentmain?.layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(all, all, all, all)
        fragmentmain.layoutParams = params

        /*---------------------Link items to Layout--------------------------*/
        cl_year = root.findViewById(R.id.cl_year)
        sp_activeyear = root.findViewById(R.id.sp_activeyear)
        val recyclerView = root.findViewById<RecyclerView>(R.id.rv_year)
        val fab = root.findViewById<FloatingActionButton>(R.id.btn_year_addYear)


        /*---------------------Fetch activeYear, and then initialize adapter--------------------------*/
        settingsViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
        yearViewModel = ViewModelProvider(this).get(YearViewModel::class.java)


        val adapter = YearListAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(root.context)

        //Automatic update the recyclerlayout
        yearViewModel.allYear.observe(viewLifecycleOwner, Observer { years ->
            years.let { adapter.submitList(it) }
            CoroutineScope(Dispatchers.IO).launch {
                setYearsToSpinner(root.context)
            }
        })


        /*---------------------FAB Add Button--------------------------*/
        fab.setOnClickListener {
            val intent = Intent(root.context, ActivityAddEditYear::class.java)
            startActivityForResult(intent, ADD_YEAR_REQUEST)
        }

        /*---------------------Swiping on a row--------------------------*/
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            /*---------------------do action on swipe--------------------------*/
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                //Item in recyclerview
                val position = viewHolder.adapterPosition
                //Item from database (yearItem?.rid gives the id)
                val yearItem = adapter.getYearAt(position)

                Log.d(TAG, "todelete year id: ${yearItem?.yid}")
                Log.d(TAG, "activeYearID: $activeYearID")

                /*---------------------Confirm Delete Dialog--------------------------*/
                if (activeYearID.equals(yearItem?.yid)) {
                    MaterialAlertDialogBuilder(context)
                            .setTitle("Achtung!")
                            .setMessage("Diese Klasse kann nicht gelöscht werden, da es die aktuell aktive Klasse ist.\n" +
                                    "Bitte ändere zunächst die aktive Klasse.")
                            .setPositiveButton("Ok") { dialog, which ->
                                adapter.notifyItemChanged(position)
                            }
                            .setOnCancelListener { dialog: DialogInterface? ->
                                adapter.notifyItemChanged(position)
                            }
                            .show()
                } else {

                    MaterialAlertDialogBuilder(context)
                            .setTitle("Achtung!")
                            .setMessage("Es wird die Klasse ${yearItem?.yname}, der Stundenplan und ALLE verknüpften Prüfungen und Aufgaben gelöscht.\n\nDas Wiederherstellen ist nicht mehr möglich!")
                            .setPositiveButton("Alles Löschen") { dialog, which ->
                                yearItem?.let { yearViewModel.delete(it) }
                                // showing snack bar with Undo option
                                val snackbar = Snackbar
                                        .make(cl_year, "Klasse ${yearItem?.yname} successfully deleted!", 8000) //ms --> 8sec
                                snackbar.show()
                            }
                            .setNegativeButton("Abbrechen") { dialog, which ->
                                adapter.notifyItemChanged(position)
                            }
                            .setOnCancelListener { dialog: DialogInterface? ->
                                adapter.notifyItemChanged(position)
                            }
                            .show()
                }
            }

            /*---------------------ADD trash bin icon to background--------------------------*/
            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

                val icon = context?.let { ContextCompat.getDrawable(it, R.drawable.ic_delete_sweep) }

                val itemView = viewHolder.itemView
                val iconMargin = (itemView.height - icon!!.intrinsicHeight) / 2
                val iconTop = itemView.top + (itemView.height - icon.intrinsicHeight) / 2
                val iconBottom = iconTop + icon.intrinsicHeight

                if (dX > 0) { // Swiping to the right
                    val iconLeft = itemView.left + iconMargin + icon.intrinsicWidth
                    val iconRight = itemView.left + iconMargin
                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)

                } else if (dX < 0) { // Swiping to the left
                    val iconLeft = itemView.right - iconMargin - icon.intrinsicWidth
                    val iconRight = itemView.right - iconMargin
                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                }

                icon.draw(c)
            }
        }).attachToRecyclerView(recyclerView)

        //Return the inflated layout
        return root
    }


    suspend fun setYearsToSpinner(root: Context) {
        /** ---------------------Create some simple Arrayadapters, to add each item...--------------------------
         * 2 Adapters for each Foreignkey, one for the Name to display, and one for the ID
         * For setting the item to the spinner:
         *  1. the *_*id Adapter is compared with the given id, and returns the position where the id is located
         *  2. this position is set to the spinner, so i get the correct name
         * For getting the selected it, it is vice versa
         */
        val year_yname = ArrayAdapter<String>(root, android.R.layout.simple_spinner_item)
        val year_yid = ArrayAdapter<Int>(root, android.R.layout.simple_spinner_item)
        /*---------------------get the list with all items in room and teacher--------------------------*/
        val year_all = yearViewModel.allYearList()

        /*---------------------add each item to the Arrayadapters--------------------------*/
        year_all.forEach {
            year_yname.add(it.yname)
            year_yid.add(it.yid)
        }

        /*---------------------Set list of all years to spinner (back in the Main thread)--------------------------*/
        withContext(Main) {


            //Get current activeYearID
            settingsViewModel.allSettings.observe(viewLifecycleOwner, Observer { setting ->
                activeYearID = setting.settings.setyid
            })
            //Thats probably not the best way to do, but now wait 100ms that the task above has finished
            delay(100)


            // Define some Properties
            sp_activeyear.setLabel("Aktive Klasse")

            // Set layout to use when the list of choices appear
            year_yid.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            year_yname.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            // Set Adapter to Spinner
            sp_activeyear.setAdapter(year_yname)

            //Set gived id's to spinner
            //How this works is explained a few lines above
            val selectedYearPos = year_yid.getPosition(activeYearID)
            selectedYearPos.let { sp_activeyear.getSpinner().setSelection(it) }

            //When selected item selected, and is not the same as already selected, then change
            sp_activeyear.getSpinner().onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    if (activeYearID != year_yid.getItem(position)!!) {
                        activeYearID = year_yid.getItem(position)!!
                        settingsViewModel.update(Settings(activeYearID))
                        val snackbar: Snackbar
                        snackbar = Snackbar
                                .make(cl_year, "Aktive Klasse geändert zu: ${year_yname.getItem(position)}", Snackbar.LENGTH_LONG)
                        snackbar.show()
                    }

                }

            }

        }
    }

    /*---------------------when returning from |ActivityAddEditYear| do something--------------------------*/
    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    @SuppressLint("ResourceAsColor")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            //Save extras to vars
            val yname = data!!.getStringExtra(ActivityAddEditYear.EXTRA_YNAME)
            val year = Year(yname)

            /*---------------------If the Request was a ADD year request--------------------------*/
            if (requestCode == ADD_YEAR_REQUEST) {

                yearViewModel.insert(year)

                /*---------------------If the Request was a EDIT year request--------------------------*/
            } else if (requestCode == EDIT_YEAR_REQUEST) {
                val id = data.getIntExtra(ActivityAddEditYear.EXTRA_YID, -1)

                if (id == -1) {
                    val snackbar: Snackbar
                    snackbar = Snackbar
                            .make(cl_year, "Failed to update Klasse!", Snackbar.LENGTH_LONG)
                    snackbar.show()
                    return
                }

                year.yid = id
                yearViewModel.update(year)

            }
        }
    }

    /*---------------------Start AddEdit Activity, to Add or Edit (this) entry--------------------------*/
    override fun onItemClicked(year: Year) {
        val intent = Intent(context, ActivityAddEditYear::class.java)
        intent.putExtra(ActivityAddEditYear.EXTRA_YID, year.yid)
        intent.putExtra(ActivityAddEditYear.EXTRA_YNAME, year.yname)
        startActivityForResult(intent, EDIT_YEAR_REQUEST)

    }


    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    fun convertDpToPixel(dp: Float, context: Context): Float {
        return dp * (context.getResources().getDisplayMetrics().densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }
}