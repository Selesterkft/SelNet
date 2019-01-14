package hu.selester.seltransport.Fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import hu.selester.seltransport.Helper.HelperClass
import hu.selester.seltransport.Helper.MySingleton
import hu.selester.seltransport.Objects.SessionClass
import hu.selester.seltransport.R
import kotlinx.android.synthetic.main.frg_login_code.view.*
import kotlinx.android.synthetic.main.frg_trasdata.view.*
import org.json.JSONArray
import org.json.JSONObject

class TransDataFragment:Fragment(){

    lateinit var rootView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.frg_trasdata, container, false)
        var addressStr = ""
        if( !SessionClass.getValue("choose_district").equals("") && !SessionClass.getValue("choose_district").equals(" ")  ) addressStr+=SessionClass.getValue("choose_district")+" "
        if( !SessionClass.getValue("choose_city").equals("") && !SessionClass.getValue("choose_city").equals(" ") ) addressStr+=SessionClass.getValue("choose_city")+" "
        rootView.transdata_name.text = SessionClass.getValue("choose_name")
        rootView.transdata_address.text = addressStr+SessionClass.getValue("choose_address")
        loadXML()
        return rootView
    }

    private fun loadXML(){
        val url = resources.getString(R.string.root_url) + "/PDA_get_addressess/1000697"
        Log.i("URL",url)
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url,null,
            Response.Listener { jsonRoot ->
                try {
                    val jsonText = jsonRoot.getString("PDA_get_addressessResult")
                    val json = JSONArray(jsonText)
                    if( jsonText.substring(0,1) == "[" ){
                        rootView.login_webview.loadData(json.getJSONObject(0).getString("html"),"text/html", "base64")
                    }else{
                        val json = JSONObject(jsonText)
                        if( !json.getString("ERROR_CODE").equals("-1") ){
                            Log.i("TAG",json.getString("ERROR_TEXT"))
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