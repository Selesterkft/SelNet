package hu.selester.selnet.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import hu.selester.selnet.helper.HelperClass
import hu.selester.selnet.helper.KeyboardUtils
import hu.selester.selnet.helper.MySingleton
import hu.selester.selnet.objects.SessionClass
import hu.selester.selnet.R
import kotlinx.android.synthetic.main.frg_verify_login.view.*
import org.json.JSONObject
import java.lang.Exception
import java.util.HashMap

class VerifyLoginFragment : Fragment() {

    lateinit var rootView: View
    var loopNum = 0
    val loopTimeSec: Long = 5000
    val MY_PERMISSIONS_REQUEST_SMS_RECEIVE = 103
    val TAG = "TAG"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        SessionClass.setValue("WSUrl", "http://185.187.72.228:8092/service1.svc")
        rootView = inflater.inflate(R.layout.frg_verify_login, container, false)
        if (HelperClass.getSharedPreferences(context!!, "verifyID") == "") {
            HelperClass.setSharedPreferences(activity!!, "logged", "0")
        } else {
            registrationRequest(HelperClass.getSharedPreferences(context!!, "verifyID"))
        }

        rootView.login_errortext.text = ""
        rootView.login_btn.setOnClickListener { login() }
        return rootView
    }

    fun login() {
        rootView.login_cc.isEnabled = false
        rootView.login_phonenumber.isEnabled = false
        rootView.login_errortext.text = ""
        loopNum = 0
        HelperClass.setSharedPreferences(
            activity!!,
            "phoneNumber",
            rootView.login_cc.selectedCountryCode + rootView.login_phonenumber.text.toString()
        )
        Log.i("TAG", "RUN LOGIN")
        smsHandler.postDelayed(smsRunnable, loopTimeSec)
        rootView.login_btn.visibility = View.GONE
        rootView.login_progressBar.visibility = View.VISIBLE
        checkForSmsPermission()
        KeyboardUtils.hideKeyboard(activity!!)
    }

    val smsHandler = Handler()
    val smsRunnable = Runnable { doJob() }

    fun doJob() {
        try {
            if (HelperClass.getSharedPreferences(activity!!, "logged") == "1") {
                registrationRequest(HelperClass.getSharedPreferences(context!!, "verifyID"))
                notLogged()
            } else {
                loopNum++
                if (loopNum > 10) {
                    rootView.login_errortext.text =
                        "Nem érkezett regisztrációs SMS a kérésre!\nKérem ellenőrizze a telefonszám helyességét!"
                    notLogged()
                } else {
                    Log.i("TAG", "LOOP")
                    smsHandler.postDelayed(smsRunnable, loopTimeSec)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun notLogged() {
        rootView.login_cc.isEnabled = true
        rootView.login_phonenumber.isEnabled = true
        rootView.login_progressBar.visibility = View.GONE
        rootView.login_btn.visibility = View.VISIBLE
    }

    fun registrationRequest(regKey: String) {
        if (HelperClass.isOnline(context!!)) {
            val map = HashMap<String, String>()
            map["Phone"] = HelperClass.getSharedPreferences(context!!, "phoneNumber")
            map["Regkey"] = regKey
            val url = SessionClass.getValue("WSUrl") + "/PDA_SMS_REG"
            Log.i("URL", "$url - $map")
            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.POST, url, JSONObject(map),
                Response.Listener { jsonRoot ->
                    try {
                        Log.i("TAG", jsonRoot.toString())
                        val json = JSONObject(jsonRoot.getString("PDA_SMS_REGResult"))
                        if (json != null) {
                            if (json.getInt("ERROR_CODE") == -1) {
                                if (json.getString("MESSAGE") == HelperClass.getSharedPreferences(
                                        context!!,
                                        "verifyID"
                                    )
                                ) {
                                    activity!!.supportFragmentManager.beginTransaction()
                                        .replace(R.id.fragment_container, TransportsListFragment())
                                        .addToBackStack("app").commit()
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    "Hiba az azonosítás közben!\nKérem próbálja meg újból!",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, Response.ErrorListener {
                    Log.i("TAG", it.message)
                }
            )
            MySingleton.getInstance(context!!).addToRequestQueue(jsonObjectRequest)
        }
    }

    private fun checkForSmsPermission() {
        if (ContextCompat.checkSelfPermission(
                context!!,
                Manifest.permission.RECEIVE_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.RECEIVE_SMS),
                MY_PERMISSIONS_REQUEST_SMS_RECEIVE
            )
        } else {
            registrationRequest("")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == MY_PERMISSIONS_REQUEST_SMS_RECEIVE) {
            if (grantResults.isEmpty()) {
                rootView.login_errortext.text =
                    "Nem tudjuk regisztrálni,\nha nem engedélyezi bejövő SMS-ek figyelését!"
                loopNum = 100
                notLogged()
                smsHandler.removeCallbacks(smsRunnable)
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                registrationRequest("")
            } else {
                rootView.login_errortext.text =
                    "Nem tudjuk regisztrálni,\nha nem engedélyezi bejövő SMS-ek figyelését!"
                loopNum = 100
                notLogged()
                smsHandler.removeCallbacks(smsRunnable)
            }
        }
    }
}