package hu.selester.seltransport.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.SmsMessage
import android.util.Log
import hu.selester.seltransport.utils.AppUtils

class SmsIncomingReceiver : BroadcastReceiver() {
    private val mTag = "SmsIncomingReceiver"
    override fun onReceive(context: Context, intent: Intent) {
        val bundle = intent.extras
        try {
            if (bundle != null) {
                val pdusObj = bundle.get("pdus") as Array<*>
                for (i in pdusObj.indices) {
                    val currentMessage: SmsMessage
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        val format = bundle.getString("format")
                        currentMessage = SmsMessage.createFromPdu(pdusObj[i] as ByteArray, format)
                    } else {
                        currentMessage = SmsMessage.createFromPdu(pdusObj[i] as ByteArray)
                    }
                    val message = currentMessage.displayMessageBody
                    val registrationKey = message.split("#")[1].substring(0, 16)

                    AppUtils.setSharedPreferences(context, "registrationKey", registrationKey)
                    AppUtils.setSharedPreferences(context, "registrationSmsReceived", "1")
                    Log.i(mTag, "RECEIVE SMS")
                }
            }
        } catch (e: Exception) {
            Log.e("SmsReceiver", "Exception smsReceiver$e")
        }
    }
}