package hu.selester.seltransport.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import hu.selester.seltransport.MainActivity
import hu.selester.seltransport.R
import hu.selester.seltransport.adapters.AddressListAdapter
import hu.selester.seltransport.database.SelTransportDatabase
import hu.selester.seltransport.database.tables.AddressesTable
import hu.selester.seltransport.database.tables.TaskActionsTable
import hu.selester.seltransport.database.tables.TransportsTable
import hu.selester.seltransport.databinding.FrgAddressListBinding

class AddressListFragment : Fragment(), AddressListAdapter.AddressListClickListener {
    private lateinit var mBinding: FrgAddressListBinding
    private lateinit var mDb: SelTransportDatabase
    private lateinit var mAddressList: List<AddressesTable>
    private val mTag = "AddressListFragment"
    private var mTransportId = 0L

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
        val activity = requireActivity() as MainActivity
        activity.title = resources.getString(R.string.addresses)
        activity.showTitleIcons()

        mBinding = FrgAddressListBinding.inflate(inflater)
        mAddressList = mDb.addressesDao().getAddressesForTransport(mTransportId)
        mBinding.addressListList.layoutManager = LinearLayoutManager(requireContext())
        mBinding.addressListList.adapter = AddressListAdapter(
            requireContext(),
            mAddressList,
            this
        )

        val spinnerAdapter = ArrayAdapter(
            requireContext(),
            R.layout.row_spinner_selected,
            arrayOf(
                getString(R.string.all_addresses),
                getString(R.string.leftover_addresses),
                getString(R.string.done_addresses)
            )
        )
        spinnerAdapter.setDropDownViewResource(R.layout.row_spinner_dropdown_item)
        mBinding.addressListSpinner.adapter = spinnerAdapter

        return mBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDb = SelTransportDatabase.getInstance(requireContext())
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