package hu.selester.seltransport.Fragments

import android.content.Context
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import hu.selester.seltransport.Adapters.LoginAdapter
import hu.selester.seltransport.BuildConfig
import hu.selester.seltransport.Database.SelTransportDatabase
import hu.selester.seltransport.Database.Tables.PhotosTable
import hu.selester.seltransport.Helper.HelperClass
import hu.selester.seltransport.Helper.MySingleton
import hu.selester.seltransport.Objects.SessionClass
import hu.selester.seltransport.R
import kotlinx.android.synthetic.main.frg_login.*
import kotlinx.android.synthetic.main.frg_login.view.*
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import java.sql.Date
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.concurrent.thread

class LoginFragment : Fragment(){

    lateinit var rootView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = LayoutInflater.from(this.context).inflate(R.layout.frg_login, container, false)
        rootView.login_version.text = "Verzió: " + BuildConfig.VERSION_NAME
        rootView.login_tabLayout.addTab(rootView.login_tabLayout.newTab().setIcon(R.drawable.tab_qr))
        rootView.login_tabLayout.addTab(rootView.login_tabLayout.newTab().setIcon(R.drawable.tab_login))

        rootView.login_viewpager.adapter = LoginAdapter(childFragmentManager)
        rootView.login_viewpager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(rootView.login_tabLayout))
        rootView.login_tabLayout.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                rootView.login_viewpager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}

            override fun onTabReselected(tab: TabLayout.Tab) {}

        })
        rootView.login_viewpager.currentItem = 0
        return rootView
    }

    override fun onDestroy() {
        super.onDestroy()
        activity!!.finish()
    }


}