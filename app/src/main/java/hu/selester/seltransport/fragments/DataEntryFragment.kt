package hu.selester.seltransport.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import hu.selester.seltransport.MainActivity
import hu.selester.seltransport.R
import hu.selester.seltransport.adapters.DataEntryAdapter
import hu.selester.seltransport.database.SelTransportDatabase
import hu.selester.seltransport.database.tables.AddressesTable
import hu.selester.seltransport.database.tables.TaskActionsTable
import hu.selester.seltransport.databinding.FrgDataEntryBinding
import hu.selester.seltransport.utils.AppUtils

class DataEntryFragment : Fragment(), DataEntryAdapter.RowSendListener {

    private lateinit var mBinding: FrgDataEntryBinding
    private lateinit var mDb: SelTransportDatabase
    private lateinit var mAddress: AddressesTable
    private lateinit var mAction: TaskActionsTable

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val activity = requireActivity() as MainActivity
        activity.title = getString(R.string.data_entry)
        activity.showTitleIcons()

        mDb = SelTransportDatabase.getInstance(requireContext())
        mBinding = FrgDataEntryBinding.inflate(inflater)

        mAddress = mDb.addressesDao().getById(requireArguments().getLong("address_id"))
        mAction = mDb.taskActionsDao().getById(requireArguments().getLong("action_id"))

        mBinding.frgDataEntryHead.partAddressHeaderLoadImage.setImageResource(
            if (mAddress.type == 1) R.drawable.address_loading
            else R.drawable.address_unloading
        )
        val cityZip = "${mAddress.country}, ${mAddress.zip}, ${mAddress.city}"
        mBinding.frgDataEntryHead.partAddressHeaderCityZip.text = cityZip
        mBinding.frgDataEntryHead.partAddressHeaderAddress.text = mAddress.address
        val dateBhr = "${AppUtils.formatDate(mAddress.date)} ${mAddress.businessHours}"
        mBinding.frgDataEntryHead.partAddressHeaderDateBusinessHours.text = dateBhr
        mBinding.frgDataEntryHead.partAddressHeaderAddressName.text = mAddress.name

        mBinding.frgDataEntryItemList.layoutManager = LinearLayoutManager(requireContext())
        mBinding.frgDataEntryItemList.adapter = DataEntryAdapter(
            requireContext(),
            mDb.actionDataFieldsDao().getByActionCpDbId(mAction.cpDbId),
            this
        )

        return mBinding.root
    }
}