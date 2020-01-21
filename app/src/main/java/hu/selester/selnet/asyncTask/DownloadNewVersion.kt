package hu.selester.selnet.asyncTask

import android.os.AsyncTask
import android.os.Environment
import android.widget.ProgressBar
import android.widget.TextView
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class DownloadNewVersion(var delegate: AsyncResponse, private val pb: ProgressBar, private val tv: TextView) :
    AsyncTask<String, Int, Int>() {

    interface AsyncResponse {
        fun processFinish(status: Int?)
    }

    override fun onProgressUpdate(vararg values: Int?) {
        super.onProgressUpdate(*values)
        pb.progress = values[0]!!
        tv.text = values[0].toString() + " %"
    }

    override fun doInBackground(vararg strings: String): Int? {
        try {
            val extStorageDirectory = Environment.getExternalStorageDirectory().toString()
            val folder = File(extStorageDirectory, "Selester")
            folder.mkdir()
            val file = File(folder, "newversion." + "apk")
            try {
                file.createNewFile()
            } catch (e1: IOException) {
                e1.printStackTrace()
            }

            val f = FileOutputStream(file)
            val u = URL(strings[0])
            val c = u.openConnection() as HttpURLConnection
            c.requestMethod = "GET"
            c.connect()
            val fileLength = c.contentLength
            var total: Long = 0
            val ins = c.inputStream
            val buffer = ByteArray(1024)
            var len1 = ins.read(buffer)
            while (len1 > 0) {
                f.write(buffer, 0, len1)
                total += len1.toLong()
                if (fileLength > 0)
                // only if total length is known
                    publishProgress((total * 100 / fileLength).toInt())
                len1 = ins.read(buffer)
            }
            f.close()

        } catch (e: Exception) {

            println("exception in DownloadFile: --------" + e.toString())
            e.printStackTrace()
            return 1
        }

        return -1
    }

    override fun onPostExecute(status: Int?) {
        delegate.processFinish(status)
    }
}