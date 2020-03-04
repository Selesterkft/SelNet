package hu.selester.selnet.threads

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import hu.selester.selnet.database.SelTransportDatabase
import hu.selester.selnet.database.tables.DocsTypeTable
import hu.selester.selnet.helper.HelperClass
import hu.selester.selnet.helper.MySingleton
import hu.selester.selnet.R
import org.json.JSONArray
import java.lang.Exception

class LoadDocTypeThread(val context:Context?):Thread(){
    override fun run() {
        if (HelperClass.isOnline(context!!)) {
            val db = SelTransportDatabase.getInstance(context)
            if( db != null ) {
                val lastID = db.docsTypeDao().getLastTransactID()
                val url = context.resources.getString(R.string.root_url) + "/Doc_Types/" + lastID
                Log.i("TAG", url)
                val jsonObjectRequest = JsonObjectRequest(
                    Request.Method.GET, url, null,
                    Response.Listener { jsonRoot ->
                        try {
                            val jsonArray = JSONArray(jsonRoot.getString("Doc_TypesResult"))
                            for (i in 0 until jsonArray.length()) {
                                val jsonObject = jsonArray.getJSONObject(i)
                                db.docsTypeDao().insert(
                                    DocsTypeTable(
                                        null,
                                        jsonObject.getInt("ID"),
                                        jsonObject.getString("Descr"),
                                        jsonObject.getInt("TransactID")
                                    )
                                )
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    },
                    Response.ErrorListener { error ->
                        Log.i("TAG", error.printStackTrace().toString())
                    })
                MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest)
            }
        }
    }
}