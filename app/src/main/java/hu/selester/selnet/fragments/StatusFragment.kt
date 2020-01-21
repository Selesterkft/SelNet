package hu.selester.selnet.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import hu.selester.selnet.R
import kotlinx.android.synthetic.main.frg_statusfragment.view.*

class StatusFragment: Fragment(){

    lateinit var rootView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.frg_statusfragment, container, false)
        rootView.status_docBtn.setOnClickListener {
            activity!!.supportFragmentManager.beginTransaction().replace(R.id.fragment_container,TransDataFragment()).addToBackStack("app").commit()
        }
        return rootView
    }
}