package hu.selester.seltransport.dialogs

import android.content.Context
import android.os.Bundle
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
    lateinit var binding: DialogTelephoneNumberBinding
    lateinit var mOnItemSelected: OnItemSelected

    interface OnItemSelected {
        fun onUserSelected(id: Long)
        fun onNewUser()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogTelephoneNumberBinding.inflate(inflater)
        val users = SelTransportDatabase.getInstance(requireContext())!!.usersDao().getValidUsers()
        binding.dialogTelNumList.layoutManager = LinearLayoutManager(requireContext())
        binding.dialogTelNumList.adapter = SimpleRowAdapter(
            requireContext(),
            users,
            this
        )

        binding.dialogTelNumNewUser.setOnClickListener {
            mOnItemSelected.onNewUser()
            dialog!!.dismiss()
        }

        return binding.root
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