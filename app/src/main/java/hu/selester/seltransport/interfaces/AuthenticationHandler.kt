package hu.selester.seltransport.interfaces

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import hu.selester.seltransport.R
import hu.selester.seltransport.database.SelTransportDatabase
import hu.selester.seltransport.objects.SessionClass
import hu.selester.seltransport.utils.AppUtils
import org.json.JSONObject
import java.util.*
import javax.net.ssl.HttpsURLConnection

interface AuthenticationHandler {
    val mContext: Context
    val mRequestUrl: String
    var mRequestJson: JSONObject
    val mTag: String

    fun doAction(responseJson: JSONObject)

    fun onError()

    fun doJob() {
        mRequestJson.put("token", SessionClass.getValue("jwt_token"))
        if (mRequestJson.getString("token") == "") {
            getNewToken()
        } else {
            val jsonObjectRequest = JsonObjectRequest(
                Request.Method.POST, mRequestUrl, mRequestJson,
                Response.Listener { resultJSON ->
                    if (resultJSON.has("error") &&
                        resultJSON.getJSONObject("error").getInt("status") == 502 &&
                        resultJSON.getJSONObject("error")
                            .getString("message") == "Expired token"
                    ) {
                        getNewToken()
                    } else {
                        doAction(resultJSON)
                    }
                },
                Response.ErrorListener { error ->
                    error.printStackTrace()
                    onError()
                })
            Volley.newRequestQueue(mContext).add(jsonObjectRequest)
        }
    }

    fun getNewToken() {
        val db = SelTransportDatabase.getInstance(mContext)
        val users = db.usersDao().getValidUserById(
            SessionClass.getValue("logged_user").toLong()
        )
        if (users.size != 1) {
            throw IndexOutOfBoundsException("AuthenticationHandler: getNewToken: user size is ${users.size}, should be 1")
        }

        val user = users[0]
        val headerJson = JSONObject()
        headerJson.put("taskType", "TKN")
        headerJson.put("interface", "1001")
        headerJson.put("sender", "droid")
        headerJson.put("recipient", "cp")

        val bodyJson = JSONObject()
        bodyJson.put("userName", user.telephoneNumber)
        bodyJson.put(
            "registrationKey", AppUtils.sha512(user.registrationKey!!).toUpperCase(
                Locale.ENGLISH
            )
        )
        bodyJson.put(
            "masterKey",
            AppUtils.sha512(user.masterKey!!).toUpperCase(Locale.ENGLISH)
        )

        val requestJson = JSONObject()
        requestJson.put("header", headerJson)
        requestJson.put("body", bodyJson)

        val url = mContext.getString(R.string.root_url) + "/getToken.php"
        Log.i("URL", url)
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, requestJson,
            Response.Listener { resultJSON ->
                Log.i(mTag, resultJSON.toString())
                val token = resultJSON.getString("token")
                SessionClass.setValue("jwt_token", token)
                onTokenArrived(token)
            },
            Response.ErrorListener { error ->
                error.printStackTrace()
                onError()
            })
        val hStack = HurlStack(null, HttpsURLConnection.getDefaultSSLSocketFactory())
        Volley.newRequestQueue(
            mContext,
            hStack
        ).add(jsonObjectRequest)
    }

    fun onTokenArrived(token: String) {
        mRequestJson.put("token", token)
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, mRequestUrl, mRequestJson,
            Response.Listener { resultJSON ->
                doAction(resultJSON)
            },
            Response.ErrorListener { error ->
                error.printStackTrace()
                onError()
            })
        Volley.newRequestQueue(mContext).add(jsonObjectRequest)
    }
}