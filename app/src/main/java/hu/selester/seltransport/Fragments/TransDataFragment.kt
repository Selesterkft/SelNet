package hu.selester.seltransport.Fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import hu.selester.seltransport.R
import kotlinx.android.synthetic.main.frg_trasdata.view.*

class TransDataFragment:Fragment(){

    lateinit var rootView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.frg_trasdata, container, false)
        val itemArray = arrayOf("Szállítólevél","CMR","Áru Árufénykép","Jegyzőkönyv")
        val arrayAdapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item,itemArray)
        rootView.transdata_docTypeSpinner.adapter = arrayAdapter
        return rootView
    }
}