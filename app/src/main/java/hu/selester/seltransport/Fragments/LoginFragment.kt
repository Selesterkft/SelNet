package hu.selester.seltransport.Fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import hu.selester.seltransport.Helper.MySingleton
import hu.selester.seltransport.R
import kotlinx.android.synthetic.main.frg_login.*
import kotlinx.android.synthetic.main.frg_login.view.*
import org.json.JSONObject

class LoginFragment : Fragment(){

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var rootView = LayoutInflater.from(this.context).inflate(R.layout.frg_login, container, false)
        rootView.login_btn.setOnClickListener {
            checkLoginParameter()
        }
        return rootView
    }

    fun checkLoginParameter(){
        val code = "7E6F4933"
        val url = resources.getString(R.string.root_url) + "/WEB_REASTAPI_USERVALIDATE_LOG_IN_TRAN/" + code
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url,null,
            Response.Listener { jsonRoot ->
                val json = JSONObject(jsonRoot.getString("WEB_REASTAPI_USERVALIDATE_LOG_IN_TRANResult") )
                if( json.getInt("ERROR_CODE") == -1 ){
                    activity!!.supportFragmentManager.beginTransaction().replace(R.id.fragment_container, WorkDatasFragment() ).addToBackStack("App").commit()
                }
            },
            Response.ErrorListener { error ->
                Log.i("TAG", error.printStackTrace().toString())
            })
        MySingleton.getInstance(context!!).addToRequestQueue(jsonObjectRequest)
    }
}