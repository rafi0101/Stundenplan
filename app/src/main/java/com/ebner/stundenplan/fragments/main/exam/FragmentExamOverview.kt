package com.ebner.stundenplan.fragments.main.exam

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ebner.stundenplan.R
import com.ebner.stundenplan.SubjectExamsActivity
import com.ebner.stundenplan.customAdapter.ExamOverviewListAdapter
import com.ebner.stundenplan.database.table.exam.ExamViewModel
import com.ebner.stundenplan.database.table.mergedEntities.SubjectTeacherRoom
import com.ebner.stundenplan.database.table.settings.SettingsViewModel
import com.ebner.stundenplan.database.table.subject.SubjectViewModel

/**
 * A simple [Fragment] subclass.
 */
class FragmentExamOverview : Fragment(), ExamOverviewListAdapter.onItemClickListener {

    private lateinit var examViewModel: ExamViewModel
    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var subjectViewModel: SubjectViewModel
    private lateinit var cl_exam: CoordinatorLayout
    private var activeYearID: Int = -1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_exam_overview, container, false)

        /*---------------------Link items to Layout--------------------------*/
        cl_exam = activity?.findViewById(R.id.cl_exam)!!
        val recyclerView = root.findViewById<RecyclerView>(R.id.rv_exam_overview)

        val adapter = ExamOverviewListAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(root.context)

        examViewModel = ViewModelProvider(this).get(ExamViewModel::class.java)
        settingsViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
        subjectViewModel = ViewModelProvider(this).get(SubjectViewModel::class.java)
        //Automatic update the recyclerlayout

        //Get current activeYearID
        settingsViewModel.allSettings.observe(viewLifecycleOwner, Observer { setting ->
            activeYearID = setting.settings.setyid

            //Set subjects list to recyclerview
            subjectViewModel.allSubject.observe(viewLifecycleOwner, Observer { subjects ->
                adapter.submitList(subjects)
            })


        })
        return root
    }

    override fun onItemClicked(subjectTeacherRoom: SubjectTeacherRoom) {
        val intent = Intent(context, SubjectExamsActivity::class.java)
        intent.putExtra(SubjectExamsActivity.EXTRA_SID, subjectTeacherRoom.subject.sid)
        intent.putExtra(SubjectExamsActivity.EXTRA_SNAME, subjectTeacherRoom.subject.sname)
        intent.putExtra(SubjectExamsActivity.EXTRA_SCOLOR, subjectTeacherRoom.subject.scolor)
        startActivity(intent)
    }

}