package hu.selester.seltransport.Fragments

import android.content.res.Resources
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import hu.selester.seltransport.Adapters.LoginAdapter
import hu.selester.seltransport.BuildConfig
import hu.selester.seltransport.Database.SelTransportDatabase
import hu.selester.seltransport.Helper.HelperClass
import hu.selester.seltransport.Helper.MySingleton
import hu.selester.seltransport.Objects.SessionClass
import hu.selester.seltransport.R
import kotlinx.android.synthetic.main.frg_login.view.*
import kotlinx.android.synthetic.main.frg_login_account.view.*
import kotlinx.android.synthetic.main.frg_setting.view.*
import org.json.JSONArray
import java.lang.Exception


class LoginFragment : Fragment(){

    lateinit var rootView: View

    fun pxToDp(num:Float):Int{
        return (num * Resources.getSystem().displayMetrics.density).toInt()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val db = SelTransportDatabase.getInstance(context!!)
        rootView = LayoutInflater.from(context).inflate(R.layout.frg_login, container, false)
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
        rootView.login_viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(p0: Int) {
            }

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
                if(p1 > 0.0 && p1 < 1.0)
                    rootView.login_backgroundPic.scrollX = pxToDp(  -100 + (200*p1)  )

            }

            override fun onPageSelected(p0: Int) {
            }
        })
        rootView.login_setting.setOnClickListener { loadSettingPanel() }
        if( db!!.systemDao().getValue("WSUrl") != null){
            if( db!!.systemDao().getValue("WSUrl").equals("") ){
                loadSettingPanel()
            }
            SessionClass.setValue("WSUrl", db.systemDao().getValue("WSUrl"))
            SessionClass.setValue("terminal", db.systemDao().getValue("terminal"))
        }else{
            loadSettingPanel()
        }


        return rootView
    }

    private fun loadSettingPanel(){
        activity!!.supportFragmentManager.beginTransaction().replace(R.id.fragment_container, SettingsFragment()).addToBackStack("app").commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        activity!!.finish()
    }

}