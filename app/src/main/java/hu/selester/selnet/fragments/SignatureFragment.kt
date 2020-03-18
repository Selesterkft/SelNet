package hu.selester.selnet.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import hu.selester.selnet.R
import hu.selester.selnet.database.SelTransportDatabase
import hu.selester.selnet.database.tables.PhotosTable
import hu.selester.selnet.database.tables.SignaturesTable
import hu.selester.selnet.threads.UploadFilesThread
import kotlinx.android.synthetic.main.dialog_signature.view.*
import java.io.*


class SignatureFragment : Fragment() {

    private val imageDir: String = "signdemo"
    private lateinit var myBitmap: Bitmap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.dialog_signature, container, false)
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
            val outputDir = File(Environment.getExternalStorageDirectory(), imageDir)
            if (!outputDir.exists()) {
                outputDir.mkdirs()
            }
            val database = SelTransportDatabase.getInstance(activity!!.applicationContext)
            val imageCount = database!!.signaturesDao().getCount()



            /*val fileketto = File(outputDir.toString(), "text.txt")
            val foStream = FileOutputStream(fileketto)
            val foStream2 = FileOutputStream(fileketto)
            try {
                foStream.write("Second line wrote 1st".toByteArray())
            } finally {
                foStream.close()
            }

            var si = fileketto.readText(Charsets.UTF_8)
            si = "First line wrote 2nd" + System.lineSeparator() + si

            try {
                foStream2.write(si.toByteArray())
            } finally {
                foStream2.close()
            }*/

            try {
                val file = File(outputDir.toString(), "$imageCount.jpg")
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
                database.photosDao().insert(
                    PhotosTable(
                        (imageCount + 1).toLong(), 123, 456, 789,
                        "Asd", "qwe", file.canonicalPath, 0, 0
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
            UploadFilesThread(this.context!!).run()
        }
        return ""
    }
}