package hu.selester.seltransport.Threads

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import hu.selester.seltransport.Database.SelTransportDatabase
import hu.selester.seltransport.Helper.Multipart
import hu.selester.seltransport.Objects.SessionClass
import java.io.File
import java.net.URL

class UploadFilesThread(val context: Context?):Thread(){

    val db: SelTransportDatabase = SelTransportDatabase.getInstance(context!!)!!

    override fun run() {
        val list = db.photosDao().getAllNotUploadedData()
        list.forEach {
            db.photosDao().setUploadStatus(it.id!!,1)
            uploadFile2(it.filePath, it.addrId, it.ptype, it.id!!)
            Log.i("TAG","OUT THREAD")
        }
    }

    override fun destroy() {
        super.destroy()
        Log.i("TAG","destroy")
    }

    private fun uploadFile2(filePathString: String, addrID: Int, docType: Int, appId : Long){
        val selectedFileUri = Uri.parse(filePathString)
        if(selectedFileUri != null){
            Thread (Runnable{
                val file = File(filePathString)
                val filename = file.path.substring(file.path.lastIndexOf("/") + 1)
                val appID = appId
                Log.i("TAG", filename)
                val options = BitmapFactory.Options()
                options.inPreferredConfig = Bitmap.Config.ARGB_8888
                val content_type = getMimeType(file.path)
                val client = Multipart(URL(SessionClass.getValue("WSUrl") + "/PostImage"))
                client.addFilePart("pic", file, filename, content_type!!)
                client.addHeaderField("addrid", addrID.toString())
                client.addHeaderField("doctypeid", docType.toString())
                Log.i("TAG", addrID.toString() + " - " + docType.toString())
                client.upload(object : Multipart.OnFileUploadedListener {
                    override fun onFileUploadingSuccess(response: String) {
                        errorUpload(appID)
                        //db.photosDao().setUploadStatus(appID,2)
                        //Log.i("TAG",""+appID)
                    }

                    override fun onFileUploadingFailed(responseCode: Int) {

                        Log.i("TAG","FAILD: "+appID)

                    }
                })
            }).start()
        }else{

        }
    }

    fun errorUpload(appID: Long){
        val photoData = db.photosDao().getDataWithID(appID)
        Log.i("TAG","tried: "+photoData.tried)
        if( photoData.tried < 6){
            db.photosDao().addTried(appID)
        }else{
            db.photosDao().setUploadStatus(appID,3)
        }
    }

    fun getRealPathFromURI(uri: Uri): String {
        var filePath = ""
        val wholeID = DocumentsContract.getDocumentId(uri)
        val id = wholeID.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
        val column = arrayOf(MediaStore.Images.Media.DATA)
        val sel = MediaStore.Images.Media._ID + "=?"
        val cursor = context!!.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, sel, arrayOf(id), null)
        val columnIndex = cursor!!.getColumnIndex(column[0])
        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex)
        }
        cursor.close()
        return filePath
    }

    private fun getMimeType(path: String): String? {
        val extension = MimeTypeMap.getFileExtensionFromUrl(path)
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
    }
}