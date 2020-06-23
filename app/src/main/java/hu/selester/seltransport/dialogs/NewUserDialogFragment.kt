package hu.selester.seltransport.dialogs

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import hu.selester.seltransport.databinding.DialogNewUserBinding

class NewUserDialogFragment : DialogFragment() {

    lateinit var mBinding: DialogNewUserBinding
    lateinit var mOnOkPressed: OnOkPressed

    interface OnOkPressed {
        fun onNewUserOkPressed(telNumber: String)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DialogNewUserBinding.inflate(inflater)
        mBinding.dialogNewUserSubmit.setOnClickListener {
            mOnOkPressed.onNewUserOkPressed(mBinding.dialogNewUserCc.selectedCountryCode + mBinding.dialogNewUserTelephoneNumber.text.toString())
            dialog!!.dismiss()
        }
        return mBinding.root
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mOnOkPressed = targetFragment as OnOkPressed
        } catch (e: ClassCastException) {
            Log.e("TelNumberDialogFragment", "onAttach: Class cast exception: ${e.message}")
        }
    }
}