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
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import hu.selester.seltransport.R
import hu.selester.seltransport.adapters.SimpleRowAdapter
import hu.selester.seltransport.database.SelTransportDatabase
import hu.selester.seltransport.database.tables.AddressesTable
import hu.selester.seltransport.databinding.FrgAddressDetailBinding
import hu.selester.seltransport.dialogs.StatusTrackingDialogFragment
import hu.selester.seltransport.utils.AppUtils

class AddressDetailFragment : Fragment(), SimpleRowAdapter.RowClickListener {
    private lateinit var mBinding: FrgAddressDetailBinding
    private lateinit var mDb: SelTransportDatabase
    private lateinit var mAddress: AddressesTable
    private val mCallPhoneRequest = 104
    private lateinit var mChosenPhoneNumber: String

    private fun callPhone(phoneNumber: String) {
        mChosenPhoneNumber = phoneNumber
        if (phoneNumber != "") {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CALL_PHONE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.CALL_PHONE),
                    mCallPhoneRequest
                )
            } else {
                val intent = Intent(Intent.ACTION_CALL)
                intent.data = Uri.parse("tel:$phoneNumber")
                requireContext().startActivity(intent)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == mCallPhoneRequest) {
            when {
                grantResults.isEmpty() -> {
                    AppUtils.toast(
                        requireContext(),
                        "Nem tud telefonhívást indítani, ha nem engedélyezi a telefonhívásokat!"
                    )
                }
                grantResults[0] == PackageManager.PERMISSION_GRANTED -> {
                    callPhone(mChosenPhoneNumber)
                }
                else -> {
                    AppUtils.toast(
                        requireContext(),
                        "Nem tud telefonhívást indítani, ha nem engedélyezi a telefonhívásokat!"
                    )
                }
            }
        }
    }


    override fun simpleRowClick(id: Long, type: Int) {
        when (type) {
            SimpleRowAdapter.TYPE_TASK_ACTION -> {
                val action = mDb.taskActionsDao().getById(id)
                val address = mDb.addressesDao().getByCpDbId(action.externalId)
                when (action.code) {
                    "nested" -> {
                        TODO("Not yet implemented")
                    }
                    "signature" -> {
                        val bundle = bundleOf(
                            "address_id" to address.id!!
                        )
                        findNavController().navigate(
                            R.id.action_addressDetailFragment_to_signatureFragment,
                            bundle
                        )
                    }
                    "scan" -> {
                        val bundle = bundleOf(
                            "address_id" to address.id!!
                        )
                        findNavController().navigate(
                            R.id.action_addressDetailFragment_to_transportPhotoFragment,
                            bundle
                        )
                    }
                    "map" -> {
                        val uri = if (address.lat == 0.0 || address.lon == 0.0) {
                            "http://maps.google.com/maps?q=${address.city}, ${address.zip}, ${address.address}(${address.name})"
                        } else {
                            "http://maps.google.com/maps?daddr= ${address.lat}, ${address.lon}(${address.name})"
                        }

                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                        intent.setPackage("com.google.android.apps.maps")
                        try {
                            startActivity(intent)
                        } catch (ex: ActivityNotFoundException) {
                            try {
                                val unrestrictedIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                                startActivity(unrestrictedIntent)
                            } catch (innerEx: ActivityNotFoundException) {
                                AppUtils.toast(
                                    requireContext(),
                                    "Please install a maps application"
                                )
                            }
                        }
                    }
                    "tracking" -> {
                        val currentStatus =
                            mDb.logisticStatusesDao().getByCpId(address.logisticStatusId)
                        val nextStatuses = mDb.logisticStatusesDao()
                            .getNextStatuses(address.transportId, address.type, currentStatus.id!!)
                        val statusTrackingDialogFragment =
                            StatusTrackingDialogFragment(nextStatuses[0].hu)
                        statusTrackingDialogFragment.setTargetFragment(this, 1)
                        statusTrackingDialogFragment.show(
                            parentFragmentManager,
                            "StatusTrackingDialogFragment"
                        )
                    }
                    "goods" -> {
                        val bundle = bundleOf(
                            "address_id" to address.id!!
                        )
                        findNavController().navigate(
                            R.id.action_addressDetailFragment_to_goodsFragment,
                            bundle
                        )
                    }
                    "get_data" -> {
                        TODO("Not yet implemented")
                    }
                    "show_info" -> {
                        TODO("Not yet implemented")
                    }
                    else -> throw java.lang.IllegalArgumentException("Unknown procedure type")
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDb = SelTransportDatabase.getInstance(requireContext())!!
        mAddress = mDb.addressesDao().getById(requireArguments().getLong("address_id"))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireActivity().title = "Műveletek"
        mBinding = FrgAddressDetailBinding.inflate(inflater)

        mBinding.frgAddressDetailsHead.partAddressHeaderLoadImage.setImageResource(
            if (mAddress.type == 1) R.drawable.address_loading
            else R.drawable.address_unloading
        )
        val cityZip = "${mAddress.country}, ${mAddress.zip}, ${mAddress.city}"
        mBinding.frgAddressDetailsHead.partAddressHeaderCityZip.text = cityZip
        mBinding.frgAddressDetailsHead.partAddressHeaderAddress.text = mAddress.address
        val dateBhr = "${AppUtils.formatDate(mAddress.date)} ${mAddress.businessHours}"
        mBinding.frgAddressDetailsHead.partAddressHeaderDateBusinessHours.text = dateBhr
        mBinding.frgAddressDetailsHead.partAddressHeaderAddressName.text = mAddress.name

        mBinding.frgAddressDetailsContactLayout.setOnClickListener {
            callPhone(mAddress.contactPhoneNumber)
        }
        mBinding.frgAddressDetailsContactName.text = mAddress.contactName
        mBinding.frgAddressDetailsContactPhone.text = mAddress.contactPhoneNumber

        if (mAddress.contactName == "" && mAddress.contactPhoneNumber == "") {
            mBinding.frgAddressDetailsContactLayout.visibility = View.GONE
        }

        mBinding.frgAddressDetailsTaskList.layoutManager = LinearLayoutManager(requireContext())
        mBinding.frgAddressDetailsTaskList.adapter = SimpleRowAdapter(
            requireContext(),
            mDb.taskActionsDao().getByExtId(mAddress.cpDbId),
            this
        )

        return mBinding.root
    }
}