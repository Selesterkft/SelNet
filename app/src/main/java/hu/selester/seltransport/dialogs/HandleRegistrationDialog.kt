package hu.selester.seltransport.dialogs

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import hu.selester.seltransport.R
import hu.selester.seltransport.databinding.DialogHandleRegistrationBinding
import java.lang.ClassCastException

class HandleRegistrationDialog() : DialogFragment() {

    private val mTag = "HandleRegDialog"
    private lateinit var mOnDeleteListener: OnDeleteListener

    interface OnDeleteListener {
        fun onDelete()
    }

    lateinit var mBinding: DialogHandleRegistrationBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DialogHandleRegistrationBinding.inflate(inflater)
        //TODO: add rest API call
        val mainText = getString(R.string.delete_registrations)
        mBinding.dialogHandleRegistrationMainText.text = mainText

        mBinding.dialogHandleRegistrationCancelButton.setOnClickListener {
            dialog!!.dismiss()
        }

        mBinding.dialogHandleRegistrationDeleteButton.setOnClickListener {
            mOnDeleteListener.onDelete()
            dialog!!.dismiss()
        }

        return mBinding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mOnDeleteListener = activity as OnDeleteListener
        } catch (e: ClassCastException) {
            Log.e(mTag, "onAttatch: ClassCastException: ${e.message}")
        }
    }
}