package hu.selester.seltransport.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import hu.selester.seltransport.databinding.DialogChangeStatusBinding

class StatusTrackingDialogFragment(private val mNewStatus: String) : DialogFragment() {
    private val mTag = "StatusTrackingDialogFragment"
    lateinit var mBinding: DialogChangeStatusBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DialogChangeStatusBinding.inflate(inflater)
        val mainText = "Biztosan megváltoztatja a logisztikai státuszt a következőre: $mNewStatus?"
        mBinding.dialogChangeStatusMainText.text = mainText

        mBinding.dialogChangeStatusCancelButton.setOnClickListener {
            dialog!!.dismiss()
        }

        mBinding.dialogChangeStatusSendButton.setOnClickListener {
            // TODO: send status to server
            dialog!!.dismiss()
        }

        return mBinding.root
    }

}