package hu.selester.selnet.Fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.zxing.integration.android.IntentIntegrator
import hu.selester.selnet.Helper.HelperClass
import hu.selester.selnet.Helper.HelperClass.Companion.toast
import hu.selester.selnet.Helper.MySingleton
import hu.selester.selnet.Objects.SessionClass
import hu.selester.selnet.R
import hu.selester.selnet.Threads.LoadDocTypeThread
import kotlinx.android.synthetic.main.frg_login_code.view.*
import org.json.JSONObject

class LoginCodeFragment : Fragment(){

    companion object {
        fun newInstance(): LoginCodeFragment{
            return LoginCodeFragment()
        }
    }

    lateinit var rootView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = LayoutInflater.from(this.context).inflate(R.layout.frg_login_code, container, false)
        rootView.login_codeBtn.setOnClickListener {
            checkLoginParameter()
        }
        //rootView.login_code.setText( "5DCD23A1" )
        rootView.qrBtn.setOnClickListener { loadQR() }
        return rootView
    }

    fun checkLoginParameter(){
        val code = rootView.login_code.text.toString()
        val url = resources.getString(R.string.root_url) + "/WEB_REASTAPI_USERVALIDATE_LOG_IN_TRAN/" + code
        Log.i("URL",url)
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url,null,
            Response.Listener { jsonRoot ->
                try {
                    val json = JSONObject(jsonRoot.getString("WEB_REASTAPI_USERVALIDATE_LOG_IN_TRANResult"))
                    Log.i("TAG",jsonRoot.getString("WEB_REASTAPI_USERVALIDATE_LOG_IN_TRANResult"))
                    if (json.getInt("ERROR_CODE") == -1) {
                        SessionClass.setValue("workCode", code)
                        SessionClass.setValue("ORD_L_ID", json.getString("ORD_L_ID"))
                        checkDOCMAN()
                    } else {
                        toast(context, json.getString("ERROR_TEXT"))
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

    private fun checkDOCMAN(){
        val code = rootView.login_code.text.toString()
        val url = resources.getString(R.string.root_url) + "/PDA_TRANSPORT_GET_DOCMAN_TABLE_PREFIX/1"
        Log.i("URL",url)
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url,null,
            Response.Listener { jsonRoot ->
                try {
                    val jsonText = jsonRoot.getString("PDA_TRANSPORT_GET_DOCMAN_TABLE_PREFIXResult")
                    val json = JSONObject(jsonText)
                    if (json.getInt("ERROR_CODE") == -1) {
                        LoadDocTypeThread(context).start()
                        activity!!.supportFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, WorkDatasFragment()).addToBackStack("App").commit()
                    } else {
                        toast(context, json.getString("ERROR_TEXT"),10000)
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

    private fun loadQR() {
        val integrator = IntentIntegrator.forSupportFragment(this)
        integrator
            .setOrientationLocked(false)
            .setBeepEnabled(true)
            .setCameraId(0)
            .setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
            .setBarcodeImageEnabled(true)
            .initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.i("WS", requestCode.toString() + " - " + resultCode)
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                    HelperClass.toast(context,"Hiba: Nem értelmezhető QR kód!")

            } else {
                Log.i("TAG",result.contents)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }

    }

}