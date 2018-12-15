package hu.selester.seltransport.Fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hu.selester.seltransport.R
import kotlinx.android.synthetic.main.frg_transports_list.view.*

class TransportsListFragment:Fragment(){

    lateinit var rootView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.frg_transports_list, container, false)
        rootView.transportlist_exit.setOnClickListener {
            fragmentManager!!.popBackStack()
        }
        return rootView
    }
}