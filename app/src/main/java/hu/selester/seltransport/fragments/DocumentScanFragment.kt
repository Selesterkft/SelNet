package hu.selester.seltransport.fragments

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
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import hu.selester.seltransport.R
import hu.selester.seltransport.adapters.PhotosListAdapter
import hu.selester.seltransport.database.SelTransportDatabase
import hu.selester.seltransport.database.tables.AddressesTable
import hu.selester.seltransport.database.tables.PhotosTable
import hu.selester.seltransport.databinding.FrgDocumentScanBinding
import hu.selester.seltransport.utils.AppUtils
import kotlinx.android.synthetic.main.dialog_show_image.view.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class DocumentScanFragment : Fragment(), PhotosListAdapter.OnItemClickListener {

    private lateinit var mBinding: FrgDocumentScanBinding
    lateinit var mDb: SelTransportDatabase
    private val mPickActionRequestCode = 100
    private val mCameraPermissionRequestCode = 200
    private val mExternalStorageRequestCode = 300
    private val mTag = "TransPhotoFragment"
    private var mCurrentPhotoPath: String = ""
    private lateinit var mAddress: AddressesTable

    override fun onItemClick(id: Long, position: Int) {
        val photo = mDb.photosDao().getById(id)

        Log.i(mTag, "SHOW PICTURES: ${photo.filePath}")
        val settingsDialog = Dialog(requireContext())
        settingsDialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        val view = layoutInflater.inflate(R.layout.dialog_show_image, null)
        view.show_picture_dialog_image.setImageBitmap(AppUtils.loadLocalImage(photo.filePath, 2))
        view.show_picture_dialog_exit.setOnClickListener {
            settingsDialog.dismiss()
        }
        view.show_picture_dialog_delete.setOnClickListener {
            onDelClick(id, position)
            settingsDialog.dismiss()
        }
        settingsDialog.setContentView(view)
        settingsDialog.show()
    }

    private fun onDelClick(id: Long, position: Int) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Biztos, hogy törli a fotót?")
        builder.setPositiveButton("IGEN") { dialog, _ ->
            mDb.photosDao().deleteById(id)
            (mBinding.transportPhotoList.adapter!! as PhotosListAdapter).removeAt(position)
            dialog.cancel()
        }
        builder.setNegativeButton("NEM") { dialog, _ ->
            dialog.cancel()
        }
        builder.show()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FrgDocumentScanBinding.inflate(inflater)
        mDb = SelTransportDatabase.getInstance(requireContext())!!
        mAddress = mDb.addressesDao().getById(requireArguments().getLong("address_id"))

        mBinding.transportPhotoCameraLayout.setOnClickListener { takePics() }
        mBinding.transportPhotoFileLayout.setOnClickListener { loadPics() }
        mBinding.transportPhotoList.layoutManager =
            GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false)
        mBinding.transportPhotoList.adapter = PhotosListAdapter(
            requireContext(),
            mDb.photosDao().getPhotosForAddress(mAddress.id!!),
            this
        )
        requireActivity().title = getString(R.string.doc_scan)

        mBinding.transportPhotoCamera.rowSimplePicture.setImageResource(R.drawable.camera)
        mBinding.transportPhotoCamera.rowSimpleText.text = getString(R.string.doc_scan)
        mBinding.transportPhotoFile.rowSimplePicture.setImageResource(R.drawable.search)
        mBinding.transportPhotoFile.rowSimpleText.text = getString(R.string.search_on_phone)

        mBinding.transphotoHead.partAddressHeaderLoadImage.setImageResource(
            if (mAddress.type == 1) R.drawable.address_loading
            else R.drawable.address_unloading
        )
        val cityZip = "${mAddress.country}, ${mAddress.zip}, ${mAddress.city}"
        mBinding.transphotoHead.partAddressHeaderCityZip.text = cityZip
        mBinding.transphotoHead.partAddressHeaderAddress.text = mAddress.address
        val dateBhr = "${AppUtils.formatDate(mAddress.date)} ${mAddress.businessHours}"
        mBinding.transphotoHead.partAddressHeaderDateBusinessHours.text = dateBhr
        mBinding.transphotoHead.partAddressHeaderAddressName.text = mAddress.name

        return mBinding.root
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        Log.i(mTag, "RequestCode - $requestCode")
        if (requestCode == mCameraPermissionRequestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePics()
            } else {
                AppUtils.toast(
                    context,
                    "Ha nem engedélyezi a kamera használatát, nem tud képeket készíteni!"
                )
            }
        }

        if (requestCode == mExternalStorageRequestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadPics()
            } else {
                AppUtils.toast(
                    context,
                    "Ha nem engedélyezi a fájlok kiválasztását, nem tud fájlokat hozzáadni!"
                )
            }
        }
    }


    private fun takePics() {
        if (ContextCompat.checkSelfPermission(
                requireActivity().baseContext,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            try {
                val f = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                    FileProvider.getUriForFile(
                        requireActivity().baseContext,
                        "hu.selester.seltransport.provider",
                        createImageFile()
                    )
                } else {
                    Uri.fromFile(createImageFile())
                }
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, f)
                startActivityForResult(takePictureIntent, mCameraPermissionRequestCode)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else {
            val permissionRequested = arrayOf(Manifest.permission.CAMERA)
            requestPermissions(permissionRequested, mCameraPermissionRequestCode)
        }
    }

    private fun loadPics() {
        if (ContextCompat.checkSelfPermission(
                requireActivity().baseContext,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.i(mTag, "loadPicsPanel")
            val photoPickerIntent = Intent(Intent.ACTION_GET_CONTENT)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, mPickActionRequestCode)
        } else {
            val permissionRequested = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            requestPermissions(permissionRequested, mExternalStorageRequestCode)
        }
    }


    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_$timeStamp"
        val storageDir =
            File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!.absolutePath)
        storageDir.mkdirs()
        val image = File.createTempFile(imageFileName, ".jpg", storageDir)
        mCurrentPhotoPath = image.absolutePath
        Log.i(mTag, mCurrentPhotoPath)
        return image
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.i(mTag, "ResultCode - $resultCode RequestCode - $requestCode")
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                mPickActionRequestCode -> {
                    Log.i(mTag, "Action_Pick")
                    val selectedImage = data!!.data
                    try {
                        Log.i(
                            mTag,
                            "" + Build.VERSION.SDK_INT + ">=" + Build.VERSION_CODES.LOLLIPOP_MR1
                        )
                        mCurrentPhotoPath =
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                                Log.i(mTag, "1")
                                getRealPathFromUri(requireActivity().baseContext, selectedImage!!)
                            } else {
                                Log.i(mTag, "2")
                                getRealPathFromUriLegacy(
                                    requireActivity().baseContext,
                                    selectedImage!!
                                )
                            }
                        if (mCurrentPhotoPath == "") {
                            mCurrentPhotoPath =
                                getRealPathFromUriLegacy(
                                    requireActivity().baseContext,
                                    selectedImage
                                )
                        }

                        Log.i(mTag, mCurrentPhotoPath)
                    } catch (e: Exception) {
                        Log.i(mTag, "Some exception ${e.printStackTrace()}")
                    }

                }
                mCameraPermissionRequestCode -> {
                    Log.i(mTag, mCurrentPhotoPath)

                }
            }
            try {
                val date = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).format(Date())
                val time = SimpleDateFormat("hh:mm", Locale.getDefault()).format(Date())
                val item = PhotosTable(
                    null,
                    mAddress.id!!,
                    date,
                    time,
                    mCurrentPhotoPath,
                    false,
                    0
                )
                mDb.photosDao().insertPhoto(item)
                (mBinding.transportPhotoList.adapter!! as PhotosListAdapter).addItem(item)
            } catch (ex: java.lang.Exception) {
                ex.printStackTrace()
                AppUtils.toast(context, "Hiba a kép mentése közben!")
            }
        }
    }

    private fun getRealPathFromUri(context: Context, uri: Uri): String {
        Log.i(mTag, uri.toString())
        var filePath = ""
        val wholeID = DocumentsContract.getDocumentId(uri)
        val id = wholeID.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
        val column = arrayOf(MediaStore.Images.Media.DATA)
        val sel = MediaStore.Images.Media._ID + "=?"
        val cursor =
            context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column,
                sel,
                arrayOf(id),
                null
            )
        val columnIndex = cursor!!.getColumnIndex(column[0])
        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex)
        }
        cursor.close()
        return filePath
    }

    private fun getRealPathFromUriLegacy(context: Context, contentUri: Uri): String {
        var cursor: Cursor? = null
        try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = context.contentResolver.query(contentUri, proj, null, null, null)
            val columnIndex = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            return cursor.getString(columnIndex)
        } finally {
            cursor?.close()
        }
    }


    override fun onResume() {
        super.onResume()
        photoListRefresh()
    }

    private fun photoListRefresh() {
        Log.i(mTag, "handler")
        (mBinding.transportPhotoList.adapter!! as PhotosListAdapter).resetList(
            mDb.photosDao().getPhotosForAddress(mAddress.id!!)
        )
    }

    /*
    private var mSelectedDocTypeId: Int = 0
    private var mSelectedDocTypeName: String = ""
    var orderId = 0
    var attrId = 0



    override fun onPause() {
        super.onPause()
        stopPhotoListRefresh()
    }

    // ----------------------------- Loop refresh handler -----------------------------------------

    var handler: Handler = Handler()
    val runnable = Runnable { photoListRefresh() }

    fun photoListRefresh() {
        Log.i(mTag, "handler")
        (mRootView.transport_photo_list.adapter!! as PhotosListAdapter).refreshList(
            mDb.photosDao().getPositionData(
                orderId,
                attrId
            ) as MutableList<PhotosTable>
        )
        val uploadItems = mDb.photosDao().getBeUploadData(orderId, attrId).size
        if (uploadItems > 0) {
            handler.postDelayed(runnable, 1000)
        }
    }

    fun stopPhotoListRefresh() {
        handler.removeCallbacks(runnable)
    }
    */

}
