package hu.selester.seltransport.dialogs

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import hu.selester.seltransport.adapters.SimpleRowAdapter
import hu.selester.seltransport.database.SelTransportDatabase
import hu.selester.seltransport.databinding.DialogTelephoneNumberBinding


class TelNumberDialogFragment : DialogFragment(), SimpleRowAdapter.RowClickListener {
    private lateinit var mBinding: DialogTelephoneNumberBinding
    private lateinit var mOnItemSelected: OnItemSelected
    private lateinit var mDb: SelTransportDatabase

    interface OnItemSelected {
        fun onUserSelected(id: Long)
        fun onNewUser()
    }

    fun resetAdapter(telephoneNumber: String) {
        val users = mDb.usersDao().getValidUserLikeNumber(telephoneNumber.toString())
        mBinding.dialogTelNumList.adapter = SimpleRowAdapter(
            requireContext(),
            users.toMutableList(),
            this
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DialogTelephoneNumberBinding.inflate(inflater)
        mDb = SelTransportDatabase.getInstance(requireContext())
        val users = mDb.usersDao().getValidUsers()
        mBinding.dialogTelNumList.layoutManager = LinearLayoutManager(requireContext())
        mBinding.dialogTelNumList.adapter = SimpleRowAdapter(
            requireContext(),
            users.toMutableList(),
            this
        )


        mBinding.dialogTelNumSearchText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                //do nothing
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                resetAdapter(s.toString())
                //do nothing
            }
        })

        mBinding.dialogTelNumNewUser.setOnClickListener {
            mOnItemSelected.onNewUser()
            dialog!!.dismiss()
        }

        return mBinding.root
    }

    override fun simpleRowClick(id: Long, type: Int) {
        when (type) {
            SimpleRowAdapter.TYPE_USER -> {
                mOnItemSelected.onUserSelected(id)
                dialog!!.dismiss()
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mOnItemSelected = targetFragment as OnItemSelected
        } catch (e: ClassCastException) {
            Log.e("TelNumberDialogFragment", "onAttach: Class cast exception: ${e.message}")
        }
    }
}