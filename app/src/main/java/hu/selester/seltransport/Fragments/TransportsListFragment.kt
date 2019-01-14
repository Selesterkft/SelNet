package hu.selester.seltransport.Fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import hu.selester.seltransport.Adapters.TransportListAdapter
import hu.selester.seltransport.Database.Tables.OrderNums
import hu.selester.seltransport.Helper.HelperClass
import hu.selester.seltransport.Helper.MySingleton
import hu.selester.seltransport.Objects.SessionClass
import hu.selester.seltransport.R
import kotlinx.android.synthetic.main.frg_login_account.view.*
import kotlinx.android.synthetic.main.frg_login_code.view.*
import kotlinx.android.synthetic.main.frg_transports_list.view.*
import org.json.JSONArray
import org.json.JSONObject

class TransportsListFragment:Fragment(), TransportListAdapter.RowClickListener{

    lateinit var rootView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.frg_transports_list, container, false)
        rootView.transportlist_exit.setOnClickListener {
            fragmentManager!!.popBackStack()
        }

        rootView.transports_list_orderList.layoutManager = LinearLayoutManager(context!!)
        loadListData()
        return rootView
    }

    override fun Click(login_code:String) {
        super.Click(login_code)
        checkLoginParameter(login_code)
    }

    fun checkLoginParameter(code: String){
        val url = resources.getString(R.string.root_url) + "/WEB_REASTAPI_USERVALIDATE_LOG_IN_TRAN/" + code
        Log.i("URL",url)
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url,null,
            Response.Listener { jsonRoot ->
                try {
                    val json = JSONObject(jsonRoot.getString("WEB_REASTAPI_USERVALIDATE_LOG_IN_TRANResult"))
                    if (json.getInt("ERROR_CODE") == -1) {
                        SessionClass.setValue("workCode", code)
                        SessionClass.setValue("ORD_L_ID", json.getString("ORD_L_ID"))
                        activity!!.supportFragmentManager.beginTransaction().replace(R.id.fragment_container, WorkDatasFragment()).addToBackStack("App").commit()
                    } else {
                        HelperClass.toast(context, json.getString("ERROR_TEXT"))
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                    HelperClass.toast(context,"Hiba a kommunikációban!")
                }
            },
            Response.ErrorListener { error ->
                error.printStackTrace()
            })
        MySingleton.getInstance(context!!).addToRequestQueue(jsonObjectRequest)
    }

    fun loadListData(){
        val terminal = SessionClass.getValue("terminal")
        val account = SessionClass.getValue("ACCOUNT")
        val password = SessionClass.getValue("PASSWORD")

        val url = resources.getString(R.string.root_url) + "/Get_Tran_TASKS/" + account + "/" + password + "/" + terminal
        Log.i("URL",url)
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url,null,
            Response.Listener { jsonRoot ->
                try {
                    val jsonText = jsonRoot.getString("Get_Tran_TASKSResult")
                    if(jsonText.substring(0,1) == "["){
                        val json = JSONArray(jsonText)
                        var dataList = mutableListOf<OrderNums>()
                        for(i in 1..(json.length()-1)){
                            dataList.add(OrderNums(null,json.getJSONObject(i).getString("LOGIN_CODE"),json.getJSONObject(i).getString("ORD_NUM")))
                        }

                        val adapter = TransportListAdapter(context!!,dataList, this)
                        rootView.transports_list_orderList.adapter = adapter
                    }else{
                        val json = JSONObject(jsonText)
                        if (json.getInt("ERROR_CODE") != -1) {
                            HelperClass.toast(context, json.getString("ERROR_TEXT"))
                        }
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                    HelperClass.toast(context,"Hiba a kommunikációban!")
                }
            },
            Response.ErrorListener { error ->
                error.printStackTrace()
            })
        MySingleton.getInstance(context!!).addToRequestQueue(jsonObjectRequest)
    }

}