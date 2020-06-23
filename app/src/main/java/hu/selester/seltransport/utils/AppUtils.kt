package hu.selester.seltransport.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.location.Address
import android.location.Geocoder
import android.media.ExifInterface
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import com.google.android.gms.maps.model.LatLng
import java.io.IOException
import java.security.MessageDigest

class AppUtils {
    companion object {
        private const val mTag = "AppUtils"

        fun sha512(input: String): String {
            return MessageDigest.getInstance("SHA-512").digest(input.toByteArray())
                .fold("", { str, it -> str + "%02x".format(it) })
        }

        fun getSharedPreferences(context: Context, key: String): String {
            val prefs = context.getSharedPreferences("selTransport", 0)

            return prefs.getString(key, "").toString()
        }

        fun setSharedPreferences(context: Context, key: String, value: String) {
            val prefs = context.getSharedPreferences("selTransport", 0)
            val editor = prefs.edit()
            editor.putString(key, value)
            editor.apply()
        }

        fun toast(context: Context?, text: String) {
            Toast.makeText(context, text, Toast.LENGTH_LONG).show()
        }

        fun toastShort(context: Context?, text: String) {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
        }

        fun formatDate(date: String): String {
            return if (date.contains("00:00")) date.substringBefore(" ") else date
        }

        fun getAndroidID(context: Context): String {
            return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        }

        fun getCoordFromAddress(context: Context?, address: String): LatLng {
            if (context != null) {
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
                Log.i(mTag, x.toString())
                return x
            } else {
                return LatLng(0.0, 0.0)
            }

        }

        fun loadLocalImage(path: String, scaledSize: Int): Bitmap? {
            //Log.i("GU","loadLocalImage: "+path);
            return getPicCorrectOrientation(path, scaledSize)
        }

        fun getPicCorrectOrientation(filePath: String, scaledSize: Int): Bitmap? {
            val options = BitmapFactory.Options()
            options.inScaled = true
            options.inSampleSize = scaledSize
            val bitmap = BitmapFactory.decodeFile(filePath, options)
            var ei: ExifInterface? = null
            try {
                ei = ExifInterface(filePath)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            val orientation = ei!!.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED
            )

            return when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90f)

                ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180f)

                ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270f)

                ExifInterface.ORIENTATION_NORMAL -> bitmap
                else -> bitmap
            }
        }

        fun rotateImage(source: Bitmap, angle: Float): Bitmap {
            val matrix = Matrix()
            matrix.postRotate(angle)
            return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
        }
    }


}