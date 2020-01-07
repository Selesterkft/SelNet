package hu.selester.selnet.Dialogs

import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.zxing.integration.android.IntentIntegrator
import hu.selester.selnet.Fragments.TransPhotoFragment
import hu.selester.selnet.Helper.HelperClass
import hu.selester.selnet.Helper.KeyboardUtils
import hu.selester.selnet.R
import kotlinx.android.synthetic.main.dialog_inputbox.view.*

class InputDialogFragment: DialogFragment(){

    lateinit var rootView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.dialog_inputbox, container, false)
        rootView.inputbox_qr.setOnClickListener {
            loadQR()
        }
        rootView.inputbox_camera.setOnClickListener {
            childFragmentManager.beginTransaction().add(R.id.inputbox_cam_container, TransPhotoFragment()).addToBackStack("app").commit()
            val displaymetrics = DisplayMetrics()
            activity!!.windowManager.defaultDisplay.getMetrics(displaymetrics)
            val screenWidth = displaymetrics.widthPixels
            val screenHeight = displaymetrics.heightPixels
            dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, screenHeight)
            KeyboardUtils.hideKeyboard(activity!!)
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

        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.i("WS", requestCode.toString() + " - " + resultCode)
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                HelperClass.toast(context,"Hiba: Nem értelmezhető QR kód!")

            } else {
                Log.i("TAG",result.contents)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }

    }

}