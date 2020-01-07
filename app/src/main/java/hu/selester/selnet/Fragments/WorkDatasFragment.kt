package hu.selester.selnet.Fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import hu.selester.selnet.Adapters.WorkDatasCardAdapter
import hu.selester.selnet.Database.SelTransportDatabase
import hu.selester.selnet.Objects.SessionClass
import hu.selester.selnet.R
import kotlinx.android.synthetic.main.frg_workdatas.view.*

class WorkDatasFragment : Fragment(){

    lateinit var rootView : View
    lateinit var db : SelTransportDatabase
    lateinit var pd: ProgressDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.frg_workdatas, container, false)
        rootView!!.workdatas_exitBtn.setOnClickListener {
            Log.i("TAG","EXIT")
            activity!!.supportFragmentManager.popBackStack()
        }
        db = SelTransportDatabase.getInstance(context!!)!!
        rootView.workdatas_fullMapBtn.setOnClickListener {
            fragmentManager!!.beginTransaction().replace(R.id.fragment_container, FullMapFragment()).addToBackStack("app").commit()
        }
        createView()
        return rootView
    }

    fun createView(){
        Log.i("TIME","createView1")
        val workDataAdapter = WorkDatasCardAdapter(childFragmentManager!!, db.tasksDAO().getOrderData(SessionClass.getValue("orderId")!!.toLong()) )
        rootView.workDatas_viewpager.adapter = workDataAdapter
        (rootView.workDatas_viewpager.adapter as WorkDatasCardAdapter).notifyDataSetChanged()
        rootView.workDatas_viewpager.clipToPadding = false
        rootView.workDatas_viewpager.pageMargin = 0
        rootView.workDatas_viewpager.offscreenPageLimit = db.transportDatasDao().getAllRowsNum() as Int
        rootView.workdatas_stepProgress.setupWithViewPager(rootView.workDatas_viewpager)
        Log.i("TIME","createView2")
    }

}