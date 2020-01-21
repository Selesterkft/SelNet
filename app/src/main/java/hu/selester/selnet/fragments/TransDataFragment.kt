package hu.selester.selnet.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import hu.selester.selnet.database.SelTransportDatabase
import hu.selester.selnet.dialogs.InputDialogFragment
import hu.selester.selnet.helper.HelperClass
import hu.selester.selnet.helper.MySingleton
import hu.selester.selnet.objects.SessionClass
import hu.selester.selnet.R
import kotlinx.android.synthetic.main.frg_trasdata.view.*
import org.json.JSONArray
import org.json.JSONObject


class TransDataFragment: Fragment(){

    lateinit var rootView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.frg_trasdata, container, false)
        var addressStr = ""
        if( !SessionClass.getValue("choose_district").equals("") && !SessionClass.getValue("choose_district").equals(" ")  ) addressStr+=SessionClass.getValue("choose_district")+" "
        if( !SessionClass.getValue("choose_city").equals("") && !SessionClass.getValue("choose_city").equals(" ") ) addressStr+=SessionClass.getValue("choose_city")+" "
        rootView.transdata_name.text = SessionClass.getValue("choose_name")
        rootView.transdata_address.text = addressStr+SessionClass.getValue("choose_address")
        val db = SelTransportDatabase.getInstance(context!!)
        val selectAddress = db!!.tasksDao().getAddressData(SessionClass.getValue("orderId")!!.toLong(),  SessionClass.getValue("choose_addressId")!!.toLong())
        rootView.login_webview.loadData(selectAddress.shortInfo,"text/html", "base64")
        rootView.transdata_expandBtn.setOnClickListener { fragmentManager!!.beginTransaction().add(R.id.fragment_container, LongInfoFragment()).addToBackStack("app").commit() }
        rootView.transdata_exit.setOnClickListener {
            fragmentManager!!.popBackStack()
        }
        addInputBtn("Megjegyzés")
        addSignitureBtn("Aláírás")
        return rootView
    }

    fun addInputBtn(text: String){
        val btn = Button(context)
        btn.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT)
        btn.text = text
        btn.setOnClickListener {
            inputDataDialog()
        }
        rootView.transdata_components.addView(btn)

    }

    fun addSignitureBtn(text: String){
        val btn = Button(context)
        btn.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT)
        btn.text = text
        btn.setOnClickListener {
            //fragmentManager!!.beginTransaction().add(R.id.fragment_container, SignatureFragment()).addToBackStack("app").commit()
            //fragmentManager!!.beginTransaction().add(R.id.fragment_container, TransPhotoFragment()).addToBackStack("app").commit()

            val inputDialogFragment = InputDialogFragment()
            val fm = activity!!.supportFragmentManager
            inputDialogFragment.show(fm, "InputDialogTag")
        }
        rootView.transdata_components.addView(btn)

    }

    private fun inputDataDialog() {
        val builder = AlertDialog.Builder(activity!!)
        builder.setTitle("Kérem adja meg a megfelelő adatot:")
        val etv = EditText(context)
        etv.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT)
        builder.setView(etv)

        builder.setPositiveButton("IGEN") { dialog, which ->
            dialog.cancel()
        }
        builder.setNegativeButton("NEM") { dialog, which ->
            dialog.cancel()
        }
        builder.show()
    }


    private fun loadXML(){
        val url = resources.getString(R.string.root_url) + "/PDA_get_addressess/1000697"
        Log.i("URL",url)
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url,null,
            Response.Listener { jsonRoot ->
                try {
                    val jsonText = jsonRoot.getString("PDA_get_addressessResult")
                    val json = JSONArray(jsonText)
                    if( jsonText.substring(0,1) == "[" ){
                        rootView.login_webview.loadData(json.getJSONObject(0).getString("html"),"text/html", "base64")
                    }else{
                        val json = JSONObject(jsonText)
                        if( !json.getString("ERROR_CODE").equals("-1") ){
                            Log.i("TAG",json.getString("ERROR_TEXT"))
                        }
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

}