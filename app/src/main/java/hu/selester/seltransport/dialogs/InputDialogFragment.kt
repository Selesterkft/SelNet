package hu.selester.seltransport.dialogs

import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.zxing.integration.android.IntentIntegrator
import hu.selester.seltransport.R
import hu.selester.seltransport.fragments.DocumentScanFragment
import hu.selester.seltransport.utils.AppUtils
import hu.selester.seltransport.utils.KeyboardUtils
import kotlinx.android.synthetic.main.dialog_inputbox.view.*

class InputDialogFragment : DialogFragment() {

    lateinit var rootView: View
    private val mTag = "InputDialogFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.dialog_inputbox, container, false)
        rootView.inputbox_qr.setOnClickListener {
            loadQR()
        }
        rootView.inputbox_camera.setOnClickListener {
            childFragmentManager.beginTransaction()
                .add(R.id.inputbox_cam_container, DocumentScanFragment()).addToBackStack("app")
                .commit()
            val displayMetrics = DisplayMetrics()
            requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
            dialog?.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                displayMetrics.heightPixels
            )
            KeyboardUtils.hideKeyboard(requireActivity())
        }
        return rootView
    }

    private fun loadQR() {
        val integrator = IntentIntegrator.forSupportFragment(this)
        integrator
            .setOrientationLocked(false)
            .setBeepEnabled(true)
            .setCameraId(0)
            .setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
            .setBarcodeImageEnabled(true)
            .initiateScan()
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.i("WS", "$requestCode - $resultCode")
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                AppUtils.toast(context, getString(R.string.unknown_qr_code))

            } else {
                Log.i(mTag, result.contents)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }

    }

}