package hu.selester.selnet.fragments

import android.graphics.Bitmap
import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import hu.selester.selnet.R
import hu.selester.selnet.database.SelTransportDatabase
import hu.selester.selnet.database.tables.SignaturesTable
import kotlinx.android.synthetic.main.dialog_signiture.view.*
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.lang.Exception


class SignatureFragment : Fragment() {

    val IMAGE_DIRECTORY: String = "signdemo"
    private lateinit var myBitmap: Bitmap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.dialog_signiture, container, false)
        rootView.signature_clear.setOnClickListener {
            rootView.signature_view.clearCanvas()
        }
        rootView.signiture_save.setOnClickListener {
            saveImage(rootView.signature_view.signatureBitmap)
        }
        return rootView
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveImageToStorage()
            } else {
                Toast.makeText(activity, "Permission not granted!", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun saveImage(bitmap: Bitmap): String {
        myBitmap = bitmap
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this.context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this.activity!!,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 100
                )
            } else {
                return saveImageToStorage()
            }

        } else {
            return saveImageToStorage()
        }
        return ""
    }

    private fun saveImageToStorage(): String {
        val externalStorageState = Environment.getExternalStorageState()
        if (externalStorageState == Environment.MEDIA_MOUNTED) {
            val outputDir = File(Environment.getExternalStorageDirectory(), IMAGE_DIRECTORY)
            if (!outputDir.exists()) {
                outputDir.mkdirs()
            }
            val database = SelTransportDatabase.getInstance(activity!!.applicationContext)
            val imageCount = database!!.signaturesDao().getCount()


            val file = File(outputDir.toString(), "$imageCount.jpg")
            try {
                val stream: OutputStream = FileOutputStream(file)
                myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                stream.flush()
                stream.close()
                Toast.makeText(
                    activity,
                    "Signature saved to ${Uri.parse(file.absolutePath)}!",
                    Toast.LENGTH_LONG
                ).show()
                view!!.signature_view.clearCanvas()
                database.signaturesDao().insert(SignaturesTable(imageCount + 1))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return ""
    }
}