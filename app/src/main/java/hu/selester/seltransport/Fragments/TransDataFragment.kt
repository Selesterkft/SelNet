package hu.selester.seltransport.Fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hu.selester.seltransport.Objects.SessionClass
import hu.selester.seltransport.R
import kotlinx.android.synthetic.main.frg_trasdata.view.*

class TransDataFragment:Fragment(){

    lateinit var rootView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.frg_trasdata, container, false)
        var addressStr = ""
        if( !SessionClass.getValue("choose_district").equals("") && !SessionClass.getValue("choose_district").equals(" ")  ) addressStr+=SessionClass.getValue("choose_district")+" "
        if( !SessionClass.getValue("choose_city").equals("") && !SessionClass.getValue("choose_city").equals(" ") ) addressStr+=SessionClass.getValue("choose_city")+" "
        rootView.transdata_name.text = SessionClass.getValue("choose_name")
        rootView.transdata_address.text = addressStr+SessionClass.getValue("choose_address")
        return rootView
    }

}