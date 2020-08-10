package hu.selester.seltransport

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.findNavController
import com.google.android.material.navigation.NavigationView
import hu.selester.seltransport.database.SelTransportDatabase
import hu.selester.seltransport.databinding.ActivityMainBinding
import hu.selester.seltransport.dialogs.ChooseLanguageDialog
import hu.selester.seltransport.dialogs.HandleRegistrationDialog
import hu.selester.seltransport.objects.SessionClass
import hu.selester.seltransport.utils.AppUtils
import hu.selester.seltransport.utils.EndDrawerToggle
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.*
import javax.net.ssl.*


@SuppressLint("TrulyRandom")
fun handleSSLHandshake() {
    try {
        val trustAllCerts: Array<TrustManager> =
            arrayOf<TrustManager>(object : X509TrustManager {
                val acceptedIssuers: Array<Any?>?
                    get() = arrayOfNulls(0)

                override fun checkClientTrusted(
                    certs: Array<X509Certificate?>?,
                    authType: String?
                ) {
                }

                override fun checkServerTrusted(
                    certs: Array<X509Certificate?>?,
                    authType: String?
                ) {
                }

                override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
                    TODO("Not yet implemented")
                }
            })
        val sc = SSLContext.getInstance("SSL")
        sc.init(null, trustAllCerts, SecureRandom())
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.socketFactory)
        HttpsURLConnection.setDefaultHostnameVerifier { arg0, arg1 -> true }
    } catch (ignored: Exception) {
    }
}


/**
 * Enables https connections
 */
fun handleSSLHandshake(resources: Resources) {
    val cFactory = CertificateFactory.getInstance("X.509")
    val certInput = resources.openRawResource(R.raw.webandtrace_crt)
    val ca = cFactory.generateCertificate(certInput) as X509Certificate

    val keyStoreType = KeyStore.getDefaultType()
    val keyStore = KeyStore.getInstance(keyStoreType).apply {
        load(null, null)
        setCertificateEntry("ca", ca)
    }

    val tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm()
    val tmf = TrustManagerFactory.getInstance(tmfAlgorithm).apply {
        init(keyStore)
    }

    val context = SSLContext.getInstance("SSL").apply {
        init(null, tmf.trustManagers, SecureRandom())
    }

    val hostnameVerifier = HostnameVerifier { _, session ->
        HttpsURLConnection.getDefaultHostnameVerifier().run {
            verify("webandtrace.com", session)
        }
    }

    HttpsURLConnection.setDefaultSSLSocketFactory(context.socketFactory)
    HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier)
}

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    HandleRegistrationDialog.OnDeleteListener, ChooseLanguageDialog.OnChooseListener {

    private lateinit var mEndDrawerToggle: EndDrawerToggle
    private lateinit var mBinding: ActivityMainBinding
    private val mTag = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleSSLHandshake()

        SessionClass.setValue("WSUrl", getString(R.string.root_url))

        mBinding = ActivityMainBinding.inflate(layoutInflater)

        setSupportActionBar(mBinding.mainToolbar.root)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        mBinding.mainToolbar.toolbarTitle.text = getString(R.string.app_name)

        mBinding.mainToolbar.toolbarBack.setOnClickListener {
            onBackPressed()
        }

        mBinding.mainNavView.itemIconTintList = null
        mBinding.mainNavView.setNavigationItemSelectedListener(this)

        mEndDrawerToggle = EndDrawerToggle(
            mBinding.mainDrawerLayout,
            mBinding.mainToolbar.root
        )

        mBinding.mainDrawerLayout.addDrawerListener(mEndDrawerToggle)
        hideTitleIcons()
        val view = mBinding.root
        setContentView(view)

        val id = SessionClass.getValue("logged_user")
        if (id != "") {
            val db = SelTransportDatabase.getInstance(this)
            setLoggedInUser(db.usersDao().getValidUserById(id.toLong())[0].telephoneNumber)
        }
    }

    override fun setTitle(title: CharSequence?) {
        mBinding.mainToolbar.toolbarTitle.text = title
    }

    fun setLoggedInUser(phoneNumber: String) {
        val header = mBinding.mainNavView.getHeaderView(0)
        val user = header.findViewById<TextView>(R.id.part_menu_username)
        val text = "+$phoneNumber"
        user.text = text
    }

    fun hideTitleIcons() {
        mBinding.mainToolbar.toolbarBack.visibility = View.GONE
    }

    fun showTitleIcons() {
        mBinding.mainToolbar.toolbarBack.visibility = View.VISIBLE
    }

    override fun onChoose(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration()
        config.setLocale(locale)
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
        recreate()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        mBinding.mainDrawerLayout.closeDrawer(GravityCompat.END)
        when (item.itemId) {
            R.id.menu_home -> {
                when (findNavController(R.id.main_nav_host_fragment).currentDestination!!.id) {
                    R.id.loginFragment -> {
                        AppUtils.toast(this, "Kérem, előbb jelentkezzen be!")
                    }
                    R.id.transportListFragment -> {
                        AppUtils.toast(this, "Jelenleg is a kezdőképernyőn tartózkodik.")
                    }
                    else -> {
                        findNavController(R.id.main_nav_host_fragment).popBackStack(
                            R.id.transportListFragment,
                            false
                        )

                    }
                }
            }
            R.id.menu_synchronize -> {
            }
            R.id.menu_clear_inactive -> {
            }
            R.id.menu_choose_language -> {
                val chooseLanguageDialog = ChooseLanguageDialog()
                chooseLanguageDialog.show(supportFragmentManager, "ChooseLanguageDialog")
            }
            R.id.menu_registration_edit -> {
                val handleRegistrationDialog = HandleRegistrationDialog()
                handleRegistrationDialog.show(supportFragmentManager, "HandleRegistrationDialog")
            }
            R.id.menu_exit -> {
                finish()
            }
        }
        return true
    }

    override fun onDelete() {
        //TODO: add rest api call
        Log.i(mTag, "onDelete")
    }
}