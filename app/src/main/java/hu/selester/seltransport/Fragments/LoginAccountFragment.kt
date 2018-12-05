package hu.selester.seltransport.Fragments

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.zxing.integration.android.IntentIntegrator
import hu.selester.seltransport.BuildConfig
import hu.selester.seltransport.Helper.HelperClass
import hu.selester.seltransport.Helper.HelperClass.Companion.toast
import hu.selester.seltransport.Helper.MySingleton
import hu.selester.seltransport.Objects.SessionClass
import hu.selester.seltransport.R
import kotlinx.android.synthetic.main.frg_login.*
import kotlinx.android.synthetic.main.frg_login.view.*
import kotlinx.android.synthetic.main.frg_login_code.view.*
import org.json.JSONObject

class LoginAccountFragment : Fragment(){

    companion object {
        fun newInstance(): LoginAccountFragment{
            return LoginAccountFragment()
        }
    }


    lateinit var rootView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = LayoutInflater.from(this.context).inflate(R.layout.frg_login_account, container, false)
        return rootView
    }
}