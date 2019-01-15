package hu.selester.seltransport.Fragments

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import hu.selester.seltransport.AsyncTask.DownloadNewVersion
import hu.selester.seltransport.BuildConfig
import hu.selester.seltransport.Helper.HelperClass
import hu.selester.seltransport.Helper.MySingleton
import hu.selester.seltransport.Objects.SessionClass
import hu.selester.seltransport.R
import hu.selester.seltransport.Threads.LoadDocTypeThread
import kotlinx.android.synthetic.main.dialog_newversion.view.*
import kotlinx.android.synthetic.main.frg_login_account.view.*
import kotlinx.android.synthetic.main.frg_login_code.view.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.lang.Exception

class LoginAccountFragment : Fragment(), DownloadNewVersion.AsyncResponse{

    companion object {
        fun newInstance(): LoginAccountFragment{
            return LoginAccountFragment()
        }
    }

    private var builder: AlertDialog.Builder? = null
    private var dialog: Dialog? = null
    private var pbSubText: TextView? = null
    private var dialogCloseBtn: Button? = null
    private var pb: ProgressBar? = null
    private var pbText: TextView? = null
    lateinit var rootView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = LayoutInflater.from(this.context).inflate(R.layout.frg_login_account, container, false)
        rootView.login_btn.setOnClickListener {
            login()
        }
        //rootView.login_account.setText("oj")
        //rootView.login_password.setText("Creative77")
        checkVersion()
        return rootView
    }

    private fun checkVersion(){
        if(HelperClass.isOnline(context!!)) {
            val url = SessionClass.getValue("WSUrl") + "/teszt_TRAN"
            Log.i("URL", url)
            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.GET, url, null,
                Response.Listener { jsonRoot ->
                    try {
                        val json = JSONObject(jsonRoot.getString("test_TRANResult"))
                        if(json != null){
                            val version = json.getDouble("VERS")
                            if( version > BuildConfig.VERSION_NAME.toDouble() ){
                                newVersionDialog(json.getString("DOWNLOAD_URL"))
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error ->
                    Log.i("TAG", error.printStackTrace().toString())
                })
            MySingleton.getInstance(context!!).addToRequestQueue(jsonObjectRequest)
        }
    }

    private fun login(){
        if(HelperClass.isOnline(context!!)) {
            val terminal = SessionClass.getValue("terminal")
            val account = rootView.login_account.text
            val password = rootView.login_password.text
            val androidID = HelperClass.getAndroidID(context!!)
            val url = SessionClass.getValue("WSUrl") + "/WEB_REASTAPI_USERVALIDATE_LOG_IN_TRAN_EMPLOYEE/"+account+"/"+password+"/"+terminal+"/"+androidID+"/0"
            Log.i("URL", url)
            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.GET, url, null,
                Response.Listener { jsonRoot ->
                    try {
                        val json = JSONObject(jsonRoot.getString("WEB_REASTAPI_USERVALIDATE_LOG_IN_TRAN_EMPLOYEE_Result"))
                        if(json != null) {
                            try {
                                if( json.getString("ERROR_CODE") != null ){
                                    when {
                                        json.getString("ERROR_CODE") == "1001" -> Toast.makeText(context!!, "Terminal megadása kötelező.", Toast.LENGTH_LONG).show()
                                        json.getString("ERROR_CODE") == "1002" -> Toast.makeText(context!!, "Adja meg a felhasználó kódját.", Toast.LENGTH_LONG).show()
                                        json.getString("ERROR_CODE") == "1003" -> Toast.makeText(context!!, "Hibás felhasználó vagy jelszó.", Toast.LENGTH_LONG).show()
                                        json.getString("ERROR_CODE") == "1004" -> Toast.makeText(context!!, "A rendszer nem engedte be, mert túllépte a megvásárolt felhasználói keretet. Léptessen ki valakit a rendszerböl.", Toast.LENGTH_LONG).show()
                                        else -> Toast.makeText(context!!, "Hibás felhasználó vagy jelszó!", Toast.LENGTH_LONG).show()
                                    }
                                }
                            } catch (e: Exception) {

                                SessionClass.setValue("USER_ID", json.getString("ID"))
                                SessionClass.setValue("USER_NAME", json.getString("Name"))
                                SessionClass.setValue("ACCOUNT", rootView.login_password.text.toString())
                                SessionClass.setValue("PASSWORD", rootView.login_account.text.toString())
                                checkDOCMAN()
                                e.printStackTrace()
                            }
                        }else{
                            Toast.makeText(context!!, "Hibás felhasználó vagy jelszó!", Toast.LENGTH_LONG).show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(context!!,"Hibás felhasználó vagy jelszó!",Toast.LENGTH_LONG).show()
                    }
                },
                Response.ErrorListener { error ->
                    Log.i("TAG", error.printStackTrace().toString())
                    Toast.makeText(context!!,"Hibás felhasználó vagy jelszó!",Toast.LENGTH_LONG).show()

                })
            MySingleton.getInstance(context!!).addToRequestQueue(jsonObjectRequest)
        }else {
            Toast.makeText(context!!,"Hiba a kapcsolódáskor!",Toast.LENGTH_LONG).show()
        }
    }

    private fun checkDOCMAN(){
        val url = resources.getString(R.string.root_url) + "/PDA_TRANSPORT_GET_DOCMAN_TABLE_PREFIX/1"
        Log.i("URL",url)
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url,null,
            Response.Listener { jsonRoot ->
                try {
                    val json = JSONObject(jsonRoot.getString("PDA_TRANSPORT_GET_DOCMAN_TABLE_PREFIXResult"))
                    if (json.getInt("ERROR_CODE") == -1) {
                        activity!!.supportFragmentManager.beginTransaction().replace(R.id.fragment_container,TransportsListFragment()).addToBackStack("app").commit()
                    } else {
                        HelperClass.toast(context, json.getString("ERROR_TEXT"), 10000)
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

    private fun newVersionDialog(url: String) {
        builder = AlertDialog.Builder(activity!!)

        val v = layoutInflater.inflate(R.layout.dialog_newversion, null)
        pb = v.newversion_progressBar
        pbText = v.newversion_progressBarPercentText!!
        pbSubText = v.newversion_progressBarSubText
        dialogCloseBtn = v.newversion_closeBtn

        v.newversion_progressBar.visibility = View.VISIBLE
        v.newversion_closeBtn.visibility = View.GONE
        v.newversion_progressBarPercentText.visibility = View.VISIBLE
        v.newversion_closeBtn.setOnClickListener { dialog!!.dismiss() }
        builder!!.setView(v)
        builder!!.setTitle("Verzió frissítés")
        dialog = builder!!.create()
        dialog!!.show()
        DownloadNewVersion(this, pb!!, pbText!!).execute(url)

    }


    override fun processFinish(status: Int?) {
        if (status == -1) {
            try {
                val f: Uri
                val intent: Intent
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                    f = FileProvider.getUriForFile(
                        activity!!.baseContext,
                        activity!!.applicationContext.packageName + ".hu.selester.android.webstockandroid.provider",
                        File(
                            Environment.getExternalStorageDirectory().toString() + "/Selester/" + "newversion.apk"
                        )
                    )
                    intent = Intent(Intent.ACTION_INSTALL_PACKAGE)
                    intent.data = f
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                } else {
                    f =
                            Uri.fromFile(File(Environment.getExternalStorageDirectory().toString() + "/Selester/" + "newversion.apk"))
                    intent = Intent(Intent.ACTION_VIEW)
                    intent.setDataAndType(f, "application/vnd.android.package-archive")
                }
                startActivity(intent)
                dialog!!.dismiss()

            } catch (e: Exception) {
                e.printStackTrace()
                pb!!.visibility = View.GONE
                dialogCloseBtn!!.visibility = View.VISIBLE
                pbText!!.visibility = View.GONE
                pbSubText!!.text = "Hiba a verzió letöltésekor,\nkérlek jelezd a Selester Kft. felé!"

            }

        } else {
            pb!!.visibility = View.GONE
            pbText!!.visibility = View.GONE
            dialogCloseBtn!!.visibility = View.VISIBLE
            pbSubText!!.text = "Hiba a verzió letöltésekor,\nkérlek jelezd a Selester Kft. felé!"
        }
    }
}