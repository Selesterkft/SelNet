package hu.selester.selnet.fragments

import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.android.volley.BuildConfig
import com.google.android.material.tabs.TabLayout
import hu.selester.selnet.adapters.LoginAdapter
import hu.selester.selnet.database.SelTransportDatabase
import hu.selester.selnet.objects.SessionClass
import hu.selester.selnet.R
import kotlinx.android.synthetic.main.frg_login.view.*


class LoginFragment : Fragment() {

    lateinit var rootView: View

    fun pxToDp(num: Float): Int {
        return (num * Resources.getSystem().displayMetrics.density).toInt()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val db = SelTransportDatabase.getInstance(context!!)
        rootView = LayoutInflater.from(context).inflate(R.layout.frg_login, container, false)
        val versionText = "Verzió" + BuildConfig.VERSION_NAME
        rootView.login_version.text = versionText
        rootView.login_tabLayout.addTab(rootView.login_tabLayout.newTab().setIcon(R.drawable.tab_qr))
        rootView.login_tabLayout.addTab(rootView.login_tabLayout.newTab().setIcon(R.drawable.tab_login))
        rootView.login_viewpager.adapter = LoginAdapter(childFragmentManager)
        rootView.login_viewpager.addOnPageChangeListener(
            TabLayout.TabLayoutOnPageChangeListener(
                rootView.login_tabLayout
            )
        )
        rootView.login_tabLayout.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                rootView.login_viewpager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}

            override fun onTabReselected(tab: TabLayout.Tab) {}

        })
        rootView.login_viewpager.currentItem = 0
        rootView.login_viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {
            }

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
                if (p1 > 0.0 && p1 < 1.0)
                    rootView.login_backgroundPic.scrollX = pxToDp(-100 + (200 * p1))

            }

            override fun onPageSelected(p0: Int) {
            }
        })
        rootView.login_setting.setOnClickListener { loadSettingPanel() }

        if (db!!.systemDao().getValue("WSUrl") == "") {
            loadSettingPanel()
        }
        SessionClass.setValue("WSUrl", db.systemDao().getValue("WSUrl"))
        SessionClass.setValue("terminal", db.systemDao().getValue("terminal"))

        return rootView
    }

    private fun loadSettingPanel() {
        /*activity!!.supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, SettingsFragment()).addToBackStack("app").commit()*/
    }

    override fun onDestroy() {
        super.onDestroy()
        activity!!.finish()
    }

}