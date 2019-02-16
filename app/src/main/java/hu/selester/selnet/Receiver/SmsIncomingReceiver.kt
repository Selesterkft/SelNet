package hu.selester.selnet.Receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.SmsMessage
import android.util.Log
import hu.selester.selnet.Helper.HelperClass

class SmsIncomingReceiver : BroadcastReceiver() {


    override fun onReceive(context: Context, intent: Intent) {
        val bundle = intent.extras
        try {
            if (bundle != null) {
                val pdusObj = bundle.get("pdus") as Array<Any>
                for (i in pdusObj.indices) {
                    val currentMessage: SmsMessage
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        val format = bundle.getString("format")
                        currentMessage = SmsMessage.createFromPdu(pdusObj[i] as ByteArray, format)
                    } else {
                        currentMessage = SmsMessage.createFromPdu(pdusObj[i] as ByteArray)
                    }
                    val phoneNumber = currentMessage.displayOriginatingAddress
                    val message = currentMessage.displayMessageBody
                    val verifyID = message.split("#")[1].substring(0,8)

                    HelperClass.setSharedPreferences(context!!,"verifyID",verifyID)
                    HelperClass.setSharedPreferences(context!!,"logged","1")
                    Log.i("TAG","RECEIVE SMS")
                }
            }
        } catch (e: Exception) {
            Log.e("SmsReceiver", "Exception smsReceiver$e")
        }
    }
}