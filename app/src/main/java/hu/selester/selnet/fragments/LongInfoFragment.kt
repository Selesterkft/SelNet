package hu.selester.selnet.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import hu.selester.selnet.database.SelTransportDatabase
import hu.selester.selnet.objects.SessionClass
import hu.selester.selnet.R
import kotlinx.android.synthetic.main.frg_longinfo.view.*

class LongInfoFragment: Fragment(){

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.frg_longinfo,container, false)
        val db = SelTransportDatabase.getInstance(context!!)
        // val selectAddress = db!!.tasksDao().getAddressData(SessionClass.getValue("orderId")!!.toLong(),  SessionClass.getValue("choose_addressId")!!.toLong())
        // rootView.longinfo_webview.loadData(selectAddress.longInfo,"text/html", "base64")
        return rootView
    }
}