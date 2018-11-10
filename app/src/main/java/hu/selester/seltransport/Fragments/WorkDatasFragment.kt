package hu.selester.seltransport.Fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hu.selester.seltransport.R
import kotlinx.android.synthetic.main.frg_workdatas.view.*

class WorkDatasFragment : Fragment(){

    var rootView : View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.frg_workdatas, container, false)
        rootView!!.workdatas_exitBtn.setOnClickListener {
            activity!!.supportFragmentManager.popBackStack()
        }
        return rootView
    }

}