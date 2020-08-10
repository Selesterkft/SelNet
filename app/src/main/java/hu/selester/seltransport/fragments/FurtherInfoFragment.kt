package hu.selester.seltransport.fragments

import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import hu.selester.seltransport.MainActivity
import hu.selester.seltransport.R
import hu.selester.seltransport.database.SelTransportDatabase
import hu.selester.seltransport.database.tables.AddressesTable
import hu.selester.seltransport.databinding.FrgFurtherInfoBinding
import hu.selester.seltransport.utils.AppUtils
import java.util.*

class FurtherInfoFragment : Fragment() {
    private lateinit var mBinding: FrgFurtherInfoBinding
    private lateinit var mDb: SelTransportDatabase
    private lateinit var mAddress: AddressesTable

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val activity = requireActivity() as MainActivity
        activity.title = getString(R.string.further_information)
        activity.showTitleIcons()

        mDb = SelTransportDatabase.getInstance(requireContext())
        mBinding = FrgFurtherInfoBinding.inflate(inflater)

        mAddress = mDb.addressesDao().getById(requireArguments().getLong("address_id"))
        mBinding.furtherInfoHead.partAddressHeaderLoadImage.setImageResource(
            if (mAddress.type == 1) R.drawable.address_loading
            else R.drawable.address_unloading
        )
        val cityZip = "${mAddress.country}, ${mAddress.zip}, ${mAddress.city}"
        mBinding.furtherInfoHead.partAddressHeaderCityZip.text = cityZip
        mBinding.furtherInfoHead.partAddressHeaderAddress.text = mAddress.address
        val dateBhr = "${AppUtils.formatDate(mAddress.date)} ${mAddress.businessHours}"
        mBinding.furtherInfoHead.partAddressHeaderDateBusinessHours.text = dateBhr
        mBinding.furtherInfoHead.partAddressHeaderAddressName.text = mAddress.name
        val webData = Base64.encodeToString(mAddress.infoField.toByteArray(Charsets.UTF_8), Base64.NO_PADDING)
        mBinding.furtherInfoWebView.loadData(webData, "text/html", "base64")

        return mBinding.root
    }
}