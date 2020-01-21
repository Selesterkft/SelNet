package hu.selester.selnet.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.zxing.integration.android.IntentIntegrator
import hu.selester.selnet.database.SelTransportDatabase
import hu.selester.selnet.database.tables.SystemTable
import hu.selester.selnet.helper.HelperClass
import hu.selester.selnet.helper.KeyboardUtils
import hu.selester.selnet.helper.MySingleton
import hu.selester.selnet.objects.SessionClass
import hu.selester.selnet.R
import kotlinx.android.synthetic.main.dialog_manual_setting.view.*
import kotlinx.android.synthetic.main.frg_setting.view.*
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception
import java.util.HashMap

class SettingsFragment: Fragment(){

    lateinit var rootView: View
    private var urlText: String = ""
    lateinit var db: SelTransportDatabase

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.frg_setting, container, false)
        db = SelTransportDatabase.getInstance(context!!)!!
        if( db.systemDao().getValue("WSUrl") != null) {
            urlText = db.systemDao().getValue("WSUrl")
            if( !urlText.equals("") ){
                rootView.setting_terminal.setText(db.systemDao().getValue("terminal"))
                chkQRConnect()
            }
        }
        rootView.setting_exit.setOnClickListener { fragmentManager!!.popBackStack() }
        rootView.setting_save.setOnClickListener { saveSetting() }
        rootView.setting_qrcamera.setOnClickListener { loadQRCamera() }
        rootView.setting_info.setOnClickListener { manualSetDialog() }
        rootView.setting_newterminal.setOnClickListener { getTerminalString(1, false) }
        return rootView
    }

    private fun loadQRCamera(){
        val integrator = IntentIntegrator.forSupportFragment(this)
        integrator
            .setOrientationLocked(false)
            .setBeepEnabled(true)
            .setCameraId(0)
            .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            .setBarcodeImageEnabled(true)
            .initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(context,"Hiba a QR kód olvasásánál!",Toast.LENGTH_LONG).show()
            } else {
                urlText = result.contents
                chkQRConnect()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }

    }

    private fun chkQRConnect() {
        if(HelperClass.isOnline(context!!)) {
            val url = urlText + "/teszt_tran"
            Log.i("URL", url)
            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.GET, url, null,
                Response.Listener { jsonRoot ->
                    try {
                        val json = JSONObject(jsonRoot.getString("test_TRANResult"))
                        if(json != null){
                            rootView.setting_qrcamera.setImageDrawable(resources.getDrawable(R.drawable.wsqr_acc,null))
                            if( rootView.setting_terminal.text.toString().equals("") ){
                                getTerminalString(0, true)
                            }
                        }else{
                            rootView.setting_qrcamera.setImageDrawable(resources.getDrawable(R.drawable.wsqr_dec,null))
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        rootView.setting_qrcamera.setImageDrawable(resources.getDrawable(R.drawable.wsqr_dec, null))
                    }
                },
                Response.ErrorListener { error ->
                    Log.i("TAG", error.printStackTrace().toString())
                    rootView.setting_qrcamera.setImageDrawable(resources.getDrawable(R.drawable.wsqr_dec, null))
                })
            MySingleton.getInstance(context!!).addToRequestQueue(jsonObjectRequest)
        }else {

        }
    }

    private fun getTerminalString(type: Int, save: Boolean) {
        val jsonObject: JSONObject? = null
        val url = urlText + "/get_freeTerminal"
        Log.i("URL", url)
        val jr = JsonObjectRequest(Request.Method.GET, url, jsonObject,
            Response.Listener { response ->
                var rootText: String?
                try {
                    rootText = response.getString("get_freeTerminalResult")
                    val jsonObject = JSONObject(rootText)
                    val terminal = jsonObject.getString("Free terminal")
                    if (terminal != null && !terminal.isEmpty()) {
                        rootView.setting_terminal.setText( terminal )
                        if (type == 1) {
                            lockTerminal(save)
                        }
                    } else {
                        Toast.makeText(context, "Nem meghatározható terminal azonosító!", Toast.LENGTH_LONG).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }, Response.ErrorListener { error ->
                error?.printStackTrace()
            })
        MySingleton.getInstance(context!!).addToRequestQueue(jr)
    }

    private fun lockTerminal(save: Boolean) {
        val url = urlText + "/SEL_SYS_INSTALLED_TERMINALS_CHECK"
        val map = HashMap<String, String>()
        map["Terminal"] = rootView.setting_terminal.text.toString()
        map["Computername"] = HelperClass.getAndroidID(context!!)
        map["StartupPath"] = "/data/data/" + context!!.packageName
        val jr = JsonObjectRequest(Request.Method.POST, url, JSONObject(map),
            Response.Listener { response ->
                try {
                    val rootText = response.getString("SEL_SYS_INSTALLED_TERMINALS_CHECKResult")
                    val jsonObject = JSONObject(rootText)
                    val rtext = jsonObject.getString("ERROR_CODE")
                    if (!rtext.isEmpty()) {
                        if (rtext == "-1") {
                            if (save) saveParamters()
                            //Toast.makeText(getContext(), "Adatok áttöltése sikeresen eltároltam!", Toast.LENGTH_LONG).show();
                        } else {
                            val etext = jsonObject.getString("ERROR_TEXT")
                            Toast.makeText(context!!, etext, Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(context!!,"Hiba a mentés során!", Toast.LENGTH_LONG).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }, Response.ErrorListener { error ->
                if (error != null) {
                    Toast.makeText(context, "Adatok áttöltése sikertelen, hálózati hiba!", Toast.LENGTH_LONG).show()
                    error.printStackTrace()
                }
            })
        MySingleton.getInstance(context!!).addToRequestQueue(jr)
    }

    internal fun saveParamters() {
        db.systemDao().setValue(SystemTable("WSUrl", urlText))
        db.systemDao().setValue(SystemTable("terminal", rootView.setting_terminal.text.toString()))
        SessionClass.setValue("WSUrl", urlText)
        SessionClass.setValue("terminal", rootView.setting_terminal.text.toString())
        Toast.makeText(context, "Mentés sikeresen megtörtént!", Toast.LENGTH_LONG).show()
        KeyboardUtils.hideKeyboard(activity!!)
        fragmentManager!!.popBackStack()
    }

    private fun saveSetting() {
        var error = ""
        if (urlText.equals("")) {
            error += "Nincs megadva a szerver host!\n\r"
        }
        if (error == "") {
            val url = urlText + "/teszt"
            val jsonObject: JSONObject? = null
            val jr = JsonObjectRequest(Request.Method.GET, url, jsonObject,
                Response.Listener { response ->
                    try {
                        val rootText = response.getString("testResult")
                        val jsonObject = JSONObject(rootText)
                        val rtext = jsonObject.getString("message")
                        if (!rtext.isEmpty()) {
                            if (rtext == "TESZT OK!") {
                                if (rootView.setting_terminal.text.toString().isEmpty()) {
                                    getTerminalString(1, true)
                                } else {
                                    saveParamters()
                                }
                                rootView.setting_qrcamera.setImageDrawable(resources.getDrawable(R.drawable.wsqr_acc, null))
                            } else {
                                Toast.makeText(context!!,"Kapcsolódás sikertelen, mentés nem lehetséges!", Toast.LENGTH_LONG).show()
                                rootView.setting_qrcamera.setImageDrawable(resources.getDrawable(R.drawable.wsqr_dec, null))
                            }
                        } else {
                            Toast.makeText(context!!,"Kapcsolódás sikertelen, mentés nem lehetséges!", Toast.LENGTH_LONG).show()
                            rootView.setting_qrcamera.setImageDrawable(resources.getDrawable(R.drawable.wsqr_dec, null))
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(context!!,"Kapcsolódás sikertelen, mentés nem lehetséges!", Toast.LENGTH_LONG).show()
                        rootView.setting_qrcamera.setImageDrawable(resources.getDrawable(R.drawable.wsqr_dec, null))
                        e.printStackTrace()
                    }
                }, Response.ErrorListener { error ->
                    if (error != null) {
                        Toast.makeText(context!!,"Kapcsolódás sikertelen, mentés nem lehetséges!", Toast.LENGTH_LONG).show()
                        rootView.setting_qrcamera.setImageDrawable(resources.getDrawable(R.drawable.wsqr_dec, null))
                        error.printStackTrace()
                    }
                })
            MySingleton.getInstance(context!!).addToRequestQueue(jr)
        } else {
            Toast.makeText(context!!,error, Toast.LENGTH_LONG).show()
            rootView.setting_qrcamera.setImageDrawable(resources.getDrawable(R.drawable.wsqr_dec, null))
        }
    }

    private fun manualSetDialog() {
        val builder = AlertDialog.Builder(activity!!)
        val v = layoutInflater.inflate(R.layout.dialog_manual_setting, null)
        val eanET = v.dialog_manual_set_ed
        eanET.setText(urlText)
        eanET.selectAll()
        builder.setView(v)
        builder.setPositiveButton("IGEN") { dialog, which ->
            KeyboardUtils.hideKeyboard(activity!!)
            urlText = eanET.text.toString()
            chkQRConnect()
            dialog.cancel()
        }
        builder.setNegativeButton("NEM") { dialog, which ->
            KeyboardUtils.hideKeyboard(activity!!)
            dialog.cancel()
        }
        builder.show()
    }
}