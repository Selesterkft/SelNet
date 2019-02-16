package hu.selester.selnet.Fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.media.MediaScannerConnection
import android.graphics.Bitmap
import android.os.Environment
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import android.widget.Toast
import hu.selester.selnet.R
import kotlinx.android.synthetic.main.dialog_signiture.view.*


class SignatureFragment:Fragment(){

    val IMAGE_DIRECTORY = "/signdemo";

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.dialog_signiture, container, false)
        rootView.signature_clear.setOnClickListener {
            rootView.signature_view.clearCanvas()
        }
        rootView.signiture_save.setOnClickListener {
            saveImage(rootView.signature_view.signatureBitmap)
        }
        return rootView
    }

    fun saveImage(myBitmap: Bitmap): String {
        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)


        val directory =
            File(Environment.getExternalStorageDirectory().toString() + java.io.File.separator + IMAGE_DIRECTORY)
        if (!directory.exists())
            Toast.makeText(activity, if (directory.mkdirs()) "Directory has been created" else "Directory not created", Toast.LENGTH_SHORT).show()
        else
            Toast.makeText(activity, "Directory exists", Toast.LENGTH_SHORT).show()

        val wallpaperDirectory = File( Environment.getExternalStorageDirectory().absolutePath + IMAGE_DIRECTORY )
        // have the object build the directory structure, if needed.

        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs()
            Log.d("filecreate", wallpaperDirectory.toString())
        }

        try {
            val f = File(wallpaperDirectory, "" + Calendar.getInstance().getTimeInMillis() + ".jpg")
            f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(
                context!!,
                arrayOf(f.getPath()),
                arrayOf("image/jpeg"), null
            )
            fo.close()
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath())

            return f.getAbsolutePath()
        } catch (e1: IOException) {
            e1.printStackTrace()
        }

        return ""

    }
}