package hu.selester.seltransport.Fragments

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.CircularProgressDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import hu.selester.seltransport.Adapters.WorkDatasCardAdapter
import hu.selester.seltransport.Database.SelTransportDatabase
import hu.selester.seltransport.Database.Tables.TransportDatasTable
import hu.selester.seltransport.Helper.HelperClass
import hu.selester.seltransport.Helper.MySingleton
import hu.selester.seltransport.Objects.SessionClass
import hu.selester.seltransport.R
import kotlinx.android.synthetic.main.frg_workdatas.*
import kotlinx.android.synthetic.main.frg_workdatas.view.*
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception

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
        loadWorkDatas()
        return rootView
    }

    fun loadWorkDatas(){
        Log.i("TIME","loadWorkDatas")
        if(HelperClass.isOnline(context!!)) {
            val code = SessionClass.getValue("ORD_L_ID")
            val url = resources.getString(R.string.root_url) + "/PDA_get_addressess/" + code
            Log.i("TAG", url)
            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.GET, url, null,
                Response.Listener { jsonRoot ->
                    try {
                        Log.i("TAG", jsonRoot.getString("PDA_get_addressessResult").substring(0, 2))
                        if (jsonRoot.getString("PDA_get_addressessResult").substring(0, 2).equals("[{")) {
                            val json = JSONArray(jsonRoot.getString("PDA_get_addressessResult"))
                            dataSaveDatabase(json)
                        } else {
                            val json = JSONObject(jsonRoot.getString("PDA_get_addressessResult"))
                            Log.i("TAG", "Object")
                            Toast.makeText(context, json.getInt("ERROR_TEXT"), Toast.LENGTH_LONG).show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(context, "Hiba a feldolgozás alatt!", Toast.LENGTH_LONG).show()
                    }
                },
                Response.ErrorListener { error ->
                    Log.i("TAG", error.printStackTrace().toString())
                })
            MySingleton.getInstance(context!!).addToRequestQueue(jsonObjectRequest)
        }else{
            createView()
        }
    }

    private fun dataSaveDatabase(jsonArray: JSONArray) {
        Log.i("TIME","dataSaveDatabase")
        db.transportDatasDao().deleteAll()
        for (i in 0..(jsonArray.length()-1) ){
            val json = jsonArray.getJSONObject(i)
            val address = json.getString("Addr") + " " + json.getString("Addr_ps_type") + " " + json.getString("Addr_housenr") + " " + json.getString("Addr_building") + " " + json.getString("Addr_stairway") + " " + json.getString("Addr_floor") + " " + json.getString("Addr_door")
            val cord = HelperClass.getLatFromAddress(context, json.getString("City") + " " + address)
            db.transportDatasDao().insert(TransportDatasTable(
                null,
                json.getString("LOGIN_CODE"),
                SessionClass.getValue("ORD_L_ID")!!,
                json.getInt("SeqNum"),
                json.getInt("AddressTypes_ID"),
                json.getInt("Addr_ID"),
                json.getString("EXPIRE"),
                json.getInt("Cust_ID"),
                json.getString("Name1"),
                json.getString("District"),
                json.getString("City"),
                address,
                cord.latitude,
                cord.longitude
                )
            )
        }
        createView()
    }

    fun createView(){
        Log.i("TIME","createView1")
        val workDataAdapter = WorkDatasCardAdapter(childFragmentManager!!, db.transportDatasDao().getAll(SessionClass.getValue("workCode")!!) )
        rootView.workDatas_viewpager.adapter = workDataAdapter
        (rootView.workDatas_viewpager.adapter as WorkDatasCardAdapter).notifyDataSetChanged()
        rootView.workDatas_viewpager.clipToPadding = false
        rootView.workDatas_viewpager.pageMargin = 0
        rootView.workDatas_viewpager.offscreenPageLimit = db.transportDatasDao().getAllRowsNum() as Int
        rootView.workdatas_stepProgress.setupWithViewPager(rootView.workDatas_viewpager)
        Log.i("TIME","createView2")
    }

}