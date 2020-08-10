package hu.selester.seltransport.dialogs

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import hu.selester.seltransport.databinding.DialogChooseLanguageBinding

class ChooseLanguageDialog : DialogFragment() {

    private val mTag = "ChooseLanguageDialog"
    private lateinit var mOnChooseListener: OnChooseListener

    interface OnChooseListener {
        fun onChoose(language: String)
    }

    lateinit var mBinding: DialogChooseLanguageBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DialogChooseLanguageBinding.inflate(inflater)

        mBinding.dialogChooseLanguageHuLayout.setOnClickListener {
            dialog!!.dismiss()
            mOnChooseListener.onChoose("hu")
        }
        mBinding.dialogChooseLanguageEnLayout.setOnClickListener {
            dialog!!.dismiss()
            mOnChooseListener.onChoose("en")
        }
        mBinding.dialogChooseLanguageDeLayout.setOnClickListener {
            dialog!!.dismiss()
            mOnChooseListener.onChoose("de")
        }

        return mBinding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mOnChooseListener = activity as OnChooseListener
        } catch (e: ClassCastException) {
            Log.e(mTag, "onAttatch: ClassCastException: ${e.message}")
        }
    }
}
