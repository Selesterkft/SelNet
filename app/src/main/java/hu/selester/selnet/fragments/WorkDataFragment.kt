package hu.selester.selnet.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import hu.selester.selnet.adapters.WorkDataCardAdapter
import hu.selester.selnet.database.SelTransportDatabase
import hu.selester.selnet.objects.SessionClass
import hu.selester.selnet.R
import kotlinx.android.synthetic.main.frg_workdata.view.*

class WorkDataFragment : Fragment(){

    lateinit var rootView : View
    lateinit var db : SelTransportDatabase

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.frg_workdata, container, false)
        db = SelTransportDatabase.getInstance(context!!)!!
        createView()
        return rootView
    }

    private fun createView(){
        Log.i("TIME","createView1")
        val workDataAdapter = WorkDataCardAdapter(childFragmentManager, db.tasksDao().getOrderData(SessionClass.getValue("orderId")!!.toLong()) )
        rootView.workDatas_viewpager.adapter = workDataAdapter
        (rootView.workDatas_viewpager.adapter as WorkDataCardAdapter).notifyDataSetChanged()
        rootView.workDatas_viewpager.clipToPadding = false
        rootView.workDatas_viewpager.pageMargin = 0
        rootView.workDatas_viewpager.offscreenPageLimit = db.transportDataDao().getAllRowsNum()
        rootView.workdatas_stepProgress.setupWithViewPager(rootView.workDatas_viewpager)
        Log.i("TIME","createView2")
    }

}