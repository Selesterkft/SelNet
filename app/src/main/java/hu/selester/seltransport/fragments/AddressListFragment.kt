package hu.selester.seltransport.fragments

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import hu.selester.seltransport.R
import hu.selester.seltransport.adapters.AddressListAdapter
import hu.selester.seltransport.database.SelTransportDatabase
import hu.selester.seltransport.database.tables.AddressesTable
import hu.selester.seltransport.database.tables.TaskActionsTable
import hu.selester.seltransport.database.tables.TransportsTable
import hu.selester.seltransport.databinding.FrgAddressListBinding
import hu.selester.seltransport.dialogs.StatusTrackingDialogFragment
import hu.selester.seltransport.utils.AppUtils

class AddressListFragment : Fragment(), AddressListAdapter.AddressListClickListener {
    private lateinit var mBinding: FrgAddressListBinding
    private lateinit var mDb: SelTransportDatabase
    private lateinit var mAddressList: List<AddressesTable>
    private val mTag = "AddressListFragment"
    var mTransportId = 0L

    override fun forwardClick(addressId: Long) {
        val address = mDb.addressesDao().getById(addressId)
        val bundle = bundleOf("address_id" to address.id)
        findNavController().navigate(
            R.id.action_addressListFragment_to_addressDetailFragment,
            bundle
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FrgAddressListBinding.inflate(inflater)
        mAddressList = mDb.addressesDao().getAddressesForTransport(mTransportId)
        mBinding.addressListList.layoutManager = LinearLayoutManager(requireContext())
        mBinding.addressListList.adapter = AddressListAdapter(
            requireContext(),
            mAddressList,
            this
        )
        mBinding.addressListSpinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_single_choice,
            arrayOf("Összes cím", "Hátralévő címek", "Kész címek")
        )

        requireActivity().title = resources.getString(R.string.addresses)
        return mBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDb = SelTransportDatabase.getInstance(requireContext())!!
        mTransportId = requireArguments().getLong("transport_id")
    }

    private fun getTaskForAction(action: TaskActionsTable): TransportsTable {
        return if (!action.isSubTask) {
            val transport = mDb.transportsDao().getData(action.externalId)
            if (transport.size != 1) {
                throw ArrayIndexOutOfBoundsException("There should be only one matching task")
            }
            transport[0]
        } else {
            val parentAction = mDb.taskActionsDao().getById(action.externalId)
            getTaskForAction(parentAction)
        }
    }
}