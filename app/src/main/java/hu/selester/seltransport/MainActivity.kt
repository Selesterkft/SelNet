package hu.selester.seltransport

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import hu.selester.seltransport.databinding.ActivityMainBinding
import hu.selester.seltransport.objects.SessionClass
import hu.selester.seltransport.utils.EndDrawerToggle
import java.security.SecureRandom
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * Enables https connections
 */
fun handleSSLHandshake() {
    try {
        val trustAllCerts: Array<TrustManager> =
            arrayOf<TrustManager>(object : X509TrustManager {
                val acceptedIssuers: Array<Any?>?
                    get() = arrayOfNulls(0)

                override fun checkClientTrusted(
                    chain: Array<out java.security.cert.X509Certificate>?,
                    authType: String?
                ) {
                }

                override fun checkServerTrusted(
                    chain: Array<out java.security.cert.X509Certificate>?,
                    authType: String?
                ) {
                }

                override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
                    TODO("Not yet implemented")
                }
            })
        val sc: SSLContext = SSLContext.getInstance("SSL")
        sc.init(null, trustAllCerts, SecureRandom())
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.socketFactory)
        HttpsURLConnection.setDefaultHostnameVerifier { _, _ -> true }
    } catch (ignored: Exception) {
    }
}

class MainActivity : AppCompatActivity() {

    private lateinit var mEndDrawerToggle: EndDrawerToggle
    private lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleSSLHandshake()
        SessionClass.setValue("WSUrl", getString(R.string.root_url))

        mBinding = ActivityMainBinding.inflate(layoutInflater)

        setSupportActionBar(mBinding.mainToolbar.root)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        mBinding.mainToolbar.toolbarTitle.text = getString(R.string.app_name)

        mBinding.mainToolbar.toolbarBack.rootView.setOnClickListener {
            onBackPressed()
        }

        mBinding.mainNavView.itemIconTintList = null

        mEndDrawerToggle = EndDrawerToggle(
            this,
            mBinding.mainDrawerLayout,
            mBinding.mainToolbar.root,
            R.string.openNavDrawer,
            R.string.closeNavDrawer
        )

        mBinding.mainDrawerLayout.addDrawerListener(mEndDrawerToggle)
        hideTitleIcons()
        val view = mBinding.root
        setContentView(view)
    }

    override fun setTitle(title: CharSequence?) {
        mBinding.mainToolbar.toolbarTitle.text = title
    }

    fun setLoggedInUser(phoneNumber: String) {
        val header = mBinding.mainNavView.getHeaderView(0)
        val user = header.findViewById<TextView>(R.id.part_menu_username)
        user.text = phoneNumber
    }

    fun hideTitleIcons() {
        mBinding.mainToolbar.toolbarBack.visibility = View.GONE
    }

    fun showTitleIcons() {
        mBinding.mainToolbar.toolbarBack.visibility = View.VISIBLE
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        mEndDrawerToggle.syncState()
    }
}