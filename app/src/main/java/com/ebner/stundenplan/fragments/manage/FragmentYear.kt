package com.ebner.stundenplan.fragments.manage

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.FrameLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
class FragmentYear : Fragment(), YearListAdapter.OnItemClickListener {

    private lateinit var yearViewModel: YearViewModel
    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var clYear: CoordinatorLayout
    private lateinit var dropdownYid: AutoCompleteTextView
    private var activeYearID: Int = -1

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
        clYear = root.findViewById(R.id.cl_year)
        dropdownYid = root.findViewById(R.id.actv_dropdown_year_yid)
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
            adapter.submitList(years)
            //This is within observer, because when you update a year, you should have immediately the dropdown menu up to date
            CoroutineScope(Dispatchers.IO).launch {
                setYearsToSpinner(root.context)
            }
        })

        /*---------------------FAB Add Button--------------------------*/
        fab.setOnClickListener {
            val intent = Intent(root.context, ActivityAddEditYear::class.java)
            openAddEditActivity.launch(intent)
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

                /*---------------------Confirm Delete Dialog--------------------------*/
                if (activeYearID == yearItem?.yid) {
                    MaterialAlertDialogBuilder(context!!)
                            .setTitle("Achtung!")
                            .setMessage("Diese Klasse kann nicht gelöscht werden, da es die aktuell aktive Klasse ist.\n" +
                                    "Bitte ändere zunächst die aktive Klasse.")
                            .setPositiveButton("Ok") { _, _ ->
                                adapter.notifyItemChanged(position)
                            }
                            .setOnCancelListener {
                                adapter.notifyItemChanged(position)
                            }
                            .show()
                } else {

                    MaterialAlertDialogBuilder(context!!)
                            .setTitle("Achtung!")
                            .setMessage("Es wird die Klasse ${yearItem?.yname}, der Stundenplan und ALLE verknüpften Prüfungen und Aufgaben gelöscht.\n\nDas Wiederherstellen ist nicht mehr möglich!")
                            .setPositiveButton("Alles Löschen") { _, _ ->
                                yearItem?.let { yearViewModel.delete(it) }
                                // showing snack bar with Undo option
                                val snackbar = Snackbar
                                        .make(clYear, "Klasse ${yearItem?.yname} successfully deleted!", 8000) //ms --> 8sec
                                snackbar.show()
                            }
                            .setNegativeButton("Abbrechen") { _, _ ->
                                adapter.notifyItemChanged(position)
                            }
                            .setOnCancelListener {
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


    private suspend fun setYearsToSpinner(root: Context) {

        /*---------------------get the list with all items in room --------------------------*/
        val yearList = yearViewModel.allYearList()

        val dropDownAdapterYear = ArrayAdapter(root, R.layout.dropdown_menu_popup_item, yearList)

        /*---------------------Set list of all years to spinner (back in the Main thread)--------------------------*/
        withContext(Main) {

            //Get current activeYearID
            settingsViewModel.allSettings.observe(viewLifecycleOwner, Observer { setting ->
                activeYearID = setting.settings.setyid
            })
            //Thats probably not the best way to do, but now wait 100ms that the task above has finished
            delay(100)

            dropdownYid.setAdapter(dropDownAdapterYear)

            //Set gived id's to spinner
            val selectedYear = yearList.first { it.yid == activeYearID }
            dropdownYid.setText(selectedYear.toString(), false)

            //When selected item selected, and is not the same as already selected, then change
            dropdownYid.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
                val activeYear = parent.adapter.getItem(position) as Year
                if (activeYearID != activeYear.yid) {
                    activeYearID = activeYear.yid
                    settingsViewModel.update(Settings(activeYearID))
                    val snackbar = Snackbar
                            .make(clYear, "Aktive Klasse geändert zu: ${activeYear.yname}", Snackbar.LENGTH_LONG)
                    snackbar.show()
                }
            }
        }
    }

    /*---------------------when returning from |ActivityAddEditYear| do something--------------------------*/
    private val openAddEditActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

        if (result.resultCode == Activity.RESULT_OK) {
            //Save extras to vars
            val data = result.data!!
            val yname = data.getStringExtra(ActivityAddEditYear.EXTRA_YNAME)!!
            val year = Year(yname)

            /*---------------------If the Request was a edit year request--------------------------*/
            if (data.hasExtra(ActivityAddEditYear.EXTRA_YID)) {
                val id = data.getIntExtra(ActivityAddEditYear.EXTRA_YID, -1)
                if (id == -1) {
                    val snackbar = Snackbar
                            .make(clYear, "Failed to update Klasse!", Snackbar.LENGTH_LONG)
                    snackbar.show()
                    return@registerForActivityResult
                }
                year.yid = id
                yearViewModel.update(year)

                /*---------------------If the Request was a add year request--------------------------*/
            } else {
                yearViewModel.insert(year)
            }
        }
    }

    /*---------------------Start AddEdit Activity, to Add or Edit (this) entry--------------------------*/
    override fun onItemClicked(year: Year) {
        val intent = Intent(context, ActivityAddEditYear::class.java)
        intent.putExtra(ActivityAddEditYear.EXTRA_YID, year.yid)
        intent.putExtra(ActivityAddEditYear.EXTRA_YNAME, year.yname)
        openAddEditActivity.launch(intent)

    }


    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    fun convertDpToPixel(dp: Float, context: Context): Float {
        return dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }
}