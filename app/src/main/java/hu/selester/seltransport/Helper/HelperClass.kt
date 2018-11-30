package hu.selester.seltransport.Helper

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.net.ConnectivityManager
import android.util.Log
import android.widget.Toast
import com.google.android.gms.maps.model.LatLng
import java.io.IOException
import java.lang.Exception

class HelperClass(){

    companion object {

        fun toast(context: Context?, text: String) {
            Toast.makeText(context, text, Toast.LENGTH_LONG).show()
        }

        fun isOnline(context: Context): Boolean {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = cm.activeNetworkInfo
            return netInfo != null && netInfo.isConnectedOrConnecting
        }

        fun getLatFromAddress(context: Context?, address: String): LatLng {
            if(context != null) {
                val latitude: Double
                val longitude: Double
                var x: LatLng

                var geocodeMatches: List<Address>? = null

                try {
                    geocodeMatches = Geocoder(context).getFromLocationName(address, 1)
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                try {
                    if (geocodeMatches != null) {
                        latitude = geocodeMatches[0].latitude
                        longitude = geocodeMatches[0].longitude
                        x = LatLng(latitude, longitude)
                    } else {
                        x = LatLng(0.0, 0.0)
                    }
                } catch (e: Exception) {
                    x = LatLng(0.0, 0.0)
                }
                Log.i("TAG", x.toString())
                return x
            }else{
                return LatLng(0.0,0.0)
            }

        }
    }
}