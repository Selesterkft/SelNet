package hu.selester.seltransport.fragments

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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import hu.selester.seltransport.MainActivity
import hu.selester.seltransport.R
import hu.selester.seltransport.database.SelTransportDatabase
import hu.selester.seltransport.database.tables.AddressesTable
import hu.selester.seltransport.databinding.FrgSignatureBinding
import hu.selester.seltransport.threads.UploadFilesThread
import hu.selester.seltransport.utils.AppUtils
import kotlinx.android.synthetic.main.frg_signature.view.*
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


class SignatureFragment : Fragment() {
    private lateinit var myBitmap: Bitmap
    private lateinit var mBinding: FrgSignatureBinding
    private lateinit var mDb: SelTransportDatabase
    private lateinit var mAddress: AddressesTable

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val activity = requireActivity() as MainActivity
        activity.title = "Aláírás"
        activity.showTitleIcons()
        mDb = SelTransportDatabase.getInstance(requireContext())!!
        mBinding = FrgSignatureBinding.inflate(inflater)

        mBinding.signatureClear.setOnClickListener {
            mBinding.signatureView.clearCanvas()
        }
        mBinding.signatureSave.setOnClickListener {
            saveImage(mBinding.signatureView.signatureBitmap)
        }

        mAddress = mDb.addressesDao().getById(requireArguments().getLong("address_id"))
        mBinding.signatureHead.partAddressHeaderLoadImage.setImageResource(
            if (mAddress.type == 1) R.drawable.address_loading
            else R.drawable.address_unloading
        )
        val cityZip = "${mAddress.country}, ${mAddress.zip}, ${mAddress.city}"
        mBinding.signatureHead.partAddressHeaderCityZip.text = cityZip
        mBinding.signatureHead.partAddressHeaderAddress.text = mAddress.address
        val dateBhr = "${AppUtils.formatDate(mAddress.date)} ${mAddress.businessHours}"
        mBinding.signatureHead.partAddressHeaderDateBusinessHours.text = dateBhr
        mBinding.signatureHead.partAddressHeaderAddressName.text = mAddress.name

        return mBinding.root
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
                AppUtils.toastShort(context, "Permission not granted!")
            }

        }
    }

    private fun saveImage(bitmap: Bitmap): String {
        myBitmap = bitmap
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this.requireActivity(),
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
            try {
                val file = File("", "")
                val stream: OutputStream = FileOutputStream(file)
                myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                stream.flush()
                stream.close()
                AppUtils.toast(
                    context,
                    "Signature saved to ${Uri.parse(file.absolutePath)}!"
                )
                requireView().signature_view.clearCanvas()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            UploadFilesThread(this.requireContext()).run()
        }
        return ""
    }
}