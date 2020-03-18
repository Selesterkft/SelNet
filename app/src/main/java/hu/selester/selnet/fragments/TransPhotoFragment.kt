package hu.selester.selnet.fragments

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import hu.selester.selnet.adapters.PhotosListAdapter
import hu.selester.selnet.database.SelTransportDatabase
import hu.selester.selnet.database.tables.PhotosTable
import hu.selester.selnet.helper.HelperClass
import hu.selester.selnet.R
import kotlinx.android.synthetic.main.dialog_show_image.view.*
import kotlinx.android.synthetic.main.frg_transphoto.view.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class TransPhotoFragment: Fragment(){

    lateinit var rootView: View
    lateinit var db:SelTransportDatabase
    var selectedDocTypeId: Int = 0
    var selectedDocTypeName: String = ""
    val REQUESTCODE_ACTION_PICK = 100
    val REQUESTCODE_ACTION_IMAGE_CAPTURE = 200
    val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 300
    private var mCurrentPhotoPath: String = ""
    val LOG_TAG = "TAG"
    var orderId = 0
    var attrId = 0


    val listener = object : PhotosListAdapter.OnItemClickListener{
        override fun onItemClick(item: String) {
            Log.i("TAG","SHOW")
            showPictureDialog(item)
        }

        override fun onDelQuestion(item: Int) {
            delQuestionDialog(item)
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.frg_transphoto, container, false)
        db = SelTransportDatabase.getInstance(context!!)!!

        rootView.transphoto_camera.setOnClickListener { takePics() }
        rootView.taskphoto_file.setOnClickListener { loadPics() }
        rootView.transphoto_list.layoutManager = LinearLayoutManager(context!!)
        val adapter = PhotosListAdapter(context as Context, db.photosDao().getPositionData( orderId, attrId ) as MutableList<PhotosTable>, listener)
        rootView.transphoto_list.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        rootView.transphoto_list.adapter = adapter
        return rootView
    }

    private fun loadPics() {
        if (ContextCompat.checkSelfPermission(activity!!.baseContext, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            loadPicsPanel()
        } else {
            val permissionRequested = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            requestPermissions(permissionRequested, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE)
        }
    }

    private fun loadPicsPanel() {
        Log.i("TAG","loadPicsPanel")
        val photoPickerIntent = Intent(Intent.ACTION_GET_CONTENT)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, REQUESTCODE_ACTION_PICK)
    }

    private fun takePics() {
        if (ContextCompat.checkSelfPermission(activity!!.baseContext, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            dispatchTakePictureIntent()
        } else {
            val permissionRequested = arrayOf(Manifest.permission.CAMERA)
            requestPermissions(permissionRequested, REQUESTCODE_ACTION_IMAGE_CAPTURE)
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.i(LOG_TAG, "RequestCode - $requestCode")
        if (requestCode == REQUESTCODE_ACTION_IMAGE_CAPTURE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent()
            } else {
                Toast.makeText(context, "NO CAMERA", Toast.LENGTH_LONG).show()
            }
        }
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            loadPicsPanel()
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(posid: Long): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_$timeStamp"
        val storageDir = File(activity!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!.absolutePath)
        storageDir.mkdirs()
        val image = File.createTempFile(imageFileName, ".jpg", storageDir)
        mCurrentPhotoPath = image.absolutePath
        Log.i(LOG_TAG, mCurrentPhotoPath)
        return image
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            val c = Calendar.getInstance()
            var f: Uri? = null
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                f = FileProvider.getUriForFile(
                    activity!!.baseContext,
                    activity!!.applicationContext.packageName + ".hu.selester.seltransport.provider",
                    createImageFile(100)
                )
            } else {
                f = Uri.fromFile(createImageFile(100))
            }
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, f)
            startActivityForResult(takePictureIntent, REQUESTCODE_ACTION_IMAGE_CAPTURE)
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.i(LOG_TAG, "ResultCode - $resultCode RequestCode - $requestCode")
        if (resultCode == Activity.RESULT_OK) {
            val c = Calendar.getInstance()
            val sdf = SimpleDateFormat("yyyy.MM.dd HH:mm")
            val dt = sdf.format(c.time)
            when (requestCode) {
                REQUESTCODE_ACTION_PICK -> {
                    Log.i(LOG_TAG, "Action_Pick")
                    val selectedImage = data!!.data
                    var mCurrPath = ""
                    try {
                        Log.i("TAG",""+Build.VERSION.SDK_INT + ">=" + Build.VERSION_CODES.LOLLIPOP_MR1)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                            Log.i("GiTa", "1")
                            mCurrPath = getRealPathFromURI(activity!!.baseContext, selectedImage!!)
                        } else {
                            Log.i("GiTa", "2")
                            mCurrPath = getRealPathFromUri2(activity!!.baseContext, selectedImage!!)
                        }
                        if (mCurrPath == "") {
                            mCurrPath = getRealPathFromUri2(activity!!.baseContext, selectedImage)
                        }

                        Log.i("TAG",mCurrPath)
                        mCurrentPhotoPath = mCurrPath
                    } catch (e: Exception) {
                        Log.i(LOG_TAG, "Some exception $e")
                    }

                }
                REQUESTCODE_ACTION_IMAGE_CAPTURE -> {
                    Log.i(LOG_TAG, mCurrentPhotoPath)

                }
            }
            try {
                val timeStamp = SimpleDateFormat("yyyy.MM.dd_HH:mm").format(Date())
                val item =  PhotosTable(
                                null,
                                orderId,
                                attrId,
                                selectedDocTypeId,
                                selectedDocTypeName,
                                timeStamp,
                                mCurrentPhotoPath,
                                0,
                                0
                            )
                db.photosDao().insert( item )
                (rootView.transphoto_list.adapter!! as PhotosListAdapter).addItem(item)
            }catch (ex:java.lang.Exception){
                ex.printStackTrace()
                HelperClass.toast(context,"Hiba a kép mentése közben!")
            }
        }
    }

    private fun getRealPathFromURI(context: Context, uri: Uri): String {
        Log.i("TAG", uri.toString())
        var filePath = ""
        val wholeID = DocumentsContract.getDocumentId(uri)
        val id = wholeID.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
        val column = arrayOf(MediaStore.Images.Media.DATA)
        val sel = MediaStore.Images.Media._ID + "=?"
        val cursor =
            context.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, sel, arrayOf(id), null)
        val columnIndex = cursor!!.getColumnIndex(column[0])
        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex)
        }
        cursor.close()
        return filePath
    }

    private fun getRealPathFromUri2(context: Context, contentUri: Uri): String {
        var cursor: Cursor? = null
        try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = context.contentResolver.query(contentUri, proj, null, null, null)
            val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            return cursor.getString(column_index)
        } finally {
            cursor?.close()
        }
    }

    private fun delQuestionDialog(position: Int) {
        val builder = AlertDialog.Builder(activity!!)
        //val v = layoutInflater.inflate(R.layout.dialog_collector, null)
        builder.setTitle("Biztos, hogy törli a fotót?")
        builder.setPositiveButton("IGEN") { dialog, which ->
            (rootView.transphoto_list.adapter!! as PhotosListAdapter).removeAt(position)
            dialog.cancel()
        }
        builder.setNegativeButton("NEM") { dialog, which ->
            dialog.cancel()
        }
        builder.show()
    }

    private fun showPictureDialog(filePath: String){
        Log.i("TAG", "SHOW PICTURES: $filePath")
        val settingsDialog = Dialog(context!!)
        settingsDialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        val view = layoutInflater.inflate(R.layout.dialog_show_image, null)
        view.show_picture_dialog_image.setImageBitmap(HelperClass.loadLocalImage(filePath,2))
        view.show_picture_dialog_btn.setOnClickListener {
            settingsDialog.dismiss()
        }
        settingsDialog.setContentView(view)
        settingsDialog.show()
    }

    override fun onResume() {
        super.onResume()
        photoListRefresh()
    }

    override fun onPause() {
        super.onPause()
        stopPhotoListRefresh()
    }

    // ----------------------------- Loop refresh handler -----------------------------------------

    var handler: Handler = Handler()
    val runnable = Runnable { photoListRefresh() }

    fun photoListRefresh() {
        Log.i("TAG","handler")
        (rootView.transphoto_list.adapter!! as PhotosListAdapter).refreshList(
            db.photosDao().getPositionData(
                orderId,
                attrId
            ) as MutableList<PhotosTable>
        )
        val uploadItems = db.photosDao().getBeUploadData(orderId,attrId).size
        if( uploadItems > 0){
            handler.postDelayed(runnable, 1000)
        }
    }

    fun stopPhotoListRefresh(){
        handler.removeCallbacks(runnable)
    }
}