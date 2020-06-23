package hu.selester.seltransport.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import hu.selester.seltransport.MainActivity
import hu.selester.seltransport.R
import hu.selester.seltransport.adapters.GoodsAdapter
import hu.selester.seltransport.database.SelTransportDatabase
import hu.selester.seltransport.database.tables.AddressesTable
import hu.selester.seltransport.databinding.FrgGoodsBinding
import hu.selester.seltransport.utils.AppUtils

class GoodsFragment : Fragment() {

    private lateinit var mBinding: FrgGoodsBinding
    private lateinit var mDb: SelTransportDatabase
    private lateinit var mAddress: AddressesTable

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val activity = requireActivity() as MainActivity
        activity.title = "Áru adatok"
        activity.showTitleIcons()
        mDb = SelTransportDatabase.getInstance(requireContext())!!
        mBinding = FrgGoodsBinding.inflate(inflater)

        mAddress = mDb.addressesDao().getById(requireArguments().getLong("address_id"))
        mBinding.trackingHead.partAddressHeaderLoadImage.setImageResource(
            if (mAddress.type == 1) R.drawable.address_loading
            else R.drawable.address_unloading
        )
        val cityZip = "${mAddress.country}, ${mAddress.zip}, ${mAddress.city}"
        mBinding.trackingHead.partAddressHeaderCityZip.text = cityZip
        mBinding.trackingHead.partAddressHeaderAddress.text = mAddress.address
        val dateBhr = "${AppUtils.formatDate(mAddress.date)} ${mAddress.businessHours}"
        mBinding.trackingHead.partAddressHeaderDateBusinessHours.text = dateBhr
        mBinding.trackingHead.partAddressHeaderAddressName.text = mAddress.name

        mBinding.trackingItemList.layoutManager = LinearLayoutManager(requireContext())
        mBinding.trackingItemList.adapter = GoodsAdapter(
            requireContext(),
            mDb.goodsDao().getByAddressId(mAddress.cpDbId)
        )

        return mBinding.root
    }
}