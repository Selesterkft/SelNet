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
import hu.selester.seltransport.MainActivity
import hu.selester.seltransport.R
import hu.selester.seltransport.adapters.DoubleRowAdapter
import hu.selester.seltransport.adapters.SimpleRowAdapter
import hu.selester.seltransport.database.SelTransportDatabase
import hu.selester.seltransport.database.tables.AddressesTable
import hu.selester.seltransport.databinding.FrgAddressDetailBinding
import hu.selester.seltransport.dialogs.SimpleDialogFragment
import hu.selester.seltransport.utils.AppUtils

class AddressDetailFragment : Fragment(), DoubleRowAdapter.RowClickListener,
    SimpleDialogFragment.ButtonListener {
    private lateinit var mBinding: FrgAddressDetailBinding
    private lateinit var mDb: SelTransportDatabase
    private lateinit var mAddress: AddressesTable
    private val mCallPhoneRequest = 104
    private lateinit var mChosenPhoneNumber: String
    private lateinit var mSimpleDialogFragment: SimpleDialogFragment

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
                        getString(R.string.no_call_permission)
                    )
                }
                grantResults[0] == PackageManager.PERMISSION_GRANTED -> {
                    callPhone(mChosenPhoneNumber)
                }
                else -> {
                    AppUtils.toast(
                        requireContext(),
                        getString(R.string.no_call_permission)
                    )
                }
            }
        }
    }


    override fun doubleRowClick(id: Long, type: Int) {
        when (type) {
            SimpleRowAdapter.TYPE_TASK_ACTION -> {
                val action = mDb.taskActionsDao().getById(id)
                val address = mDb.addressesDao().getByCpDbId(action.externalId)
                when (action.code) {
                    AppUtils.NESTED -> {
                        TODO("Not yet implemented")
                    }
                    AppUtils.SIGNATURE -> {
                        val bundle = bundleOf(
                            "address_id" to address.id!!
                        )
                        findNavController().navigate(
                            R.id.action_addressDetailFragment_to_signatureFragment,
                            bundle
                        )
                    }
                    AppUtils.SCAN -> {
                        val bundle = bundleOf(
                            "address_id" to address.id!!
                        )
                        findNavController().navigate(
                            R.id.action_addressDetailFragment_to_transportPhotoFragment,
                            bundle
                        )
                    }
                    AppUtils.MAP -> {
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
                                    getString(R.string.no_map_app)
                                )
                            }
                        }
                    }
                    AppUtils.TRACKING -> {
                        val currentStatus =
                            mDb.logisticStatusesDao().getByCpId(address.logisticStatusId)
                        val nextStatuses = mDb.logisticStatusesDao()
                            .getNextStatuses(address.transportId, address.type, currentStatus.id!!)
                        val message =
                            getString(R.string.change_logistic_status, AppUtils.getStatusName(nextStatuses[0], requireContext()))
                        mSimpleDialogFragment =
                            SimpleDialogFragment(
                                this,
                                getString(R.string.tracking),
                                message,
                                resources.getString(R.string.cancel),
                                resources.getString(R.string.set)
                            )
                        mSimpleDialogFragment.setTargetFragment(this, 1)
                        mSimpleDialogFragment.show(
                            parentFragmentManager,
                            "StatusTrackingDialogFragment"
                        )
                    }
                    AppUtils.GOODS -> {
                        val bundle = bundleOf(
                            "address_id" to address.id!!
                        )
                        findNavController().navigate(
                            R.id.action_addressDetailFragment_to_goodsFragment,
                            bundle
                        )
                    }
                    AppUtils.GET_DATA -> {
                        val bundle = bundleOf(
                            "address_id" to address.id!!,
                            "action_id" to action.id!!
                        )
                        findNavController().navigate(
                            R.id.action_addressDetailFragment_to_dataEntryFragment,
                            bundle
                        )
                    }
                    AppUtils.SHOW_INFO -> {
                        val bundle = bundleOf(
                            "address_id" to address.id!!
                        )
                        findNavController().navigate(
                            R.id.action_addressDetailFragment_to_furtherInfoFragment,
                            bundle
                        )
                    }
                    else -> throw IllegalArgumentException("Unknown procedure type")
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDb = SelTransportDatabase.getInstance(requireContext())
        mAddress = mDb.addressesDao().getById(requireArguments().getLong("address_id"))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val activity = requireActivity() as MainActivity
        activity.title = getString(R.string.procedures)
        activity.showTitleIcons()

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
        mBinding.frgAddressDetailsTaskList.adapter = DoubleRowAdapter(
            requireContext(),
            mDb.taskActionsDao().getByExtId(mAddress.cpDbId).toMutableList(),
            this
        )

        return mBinding.root
    }

    override fun onLeftClicked() {
        mSimpleDialogFragment.dismiss()
    }

    override fun onRightClicked() {
        // TODO: send status to server
        mSimpleDialogFragment.dismiss()
    }
}