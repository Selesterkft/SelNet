package hu.selester.seltransport.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import hu.selester.seltransport.databinding.DialogSimpleBinding

class SimpleDialogFragment(
    private val mButtonListener: ButtonListener,
    private val mTitle: String,
    private val mMainText: String,
    private val mLeftButtonText: String,
    private val mRightButtonText: String
) : DialogFragment() {
    private val mTag = "StatusTrackingDialogFragment"
    lateinit var mBinding: DialogSimpleBinding

    interface ButtonListener {
        fun onLeftClicked()
        fun onRightClicked()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DialogSimpleBinding.inflate(inflater)
        mBinding.dialogSimpleTitle.text = mTitle
        mBinding.dialogSimpleMainText.text = mMainText

        mBinding.dialogSimpleLeftButton.text = mLeftButtonText
        mBinding.dialogSimpleLeftButton.setOnClickListener {
            mButtonListener.onLeftClicked()
        }

        mBinding.dialogSimpleRightButton.text = mRightButtonText
        mBinding.dialogSimpleRightButton.setOnClickListener {
            mButtonListener.onRightClicked()
        }

        return mBinding.root
    }

}