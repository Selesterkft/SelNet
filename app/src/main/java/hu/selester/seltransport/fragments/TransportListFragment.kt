package hu.selester.seltransport.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import hu.selester.seltransport.adapters.TransportListAdapter
import hu.selester.seltransport.database.SelTransportDatabase
import hu.selester.seltransport.database.tables.*
import hu.selester.seltransport.databinding.FrgTransportListBinding
import hu.selester.seltransport.dialogs.SimpleDialogFragment
import hu.selester.seltransport.interfaces.AuthenticationHandler
import hu.selester.seltransport.objects.SessionClass
import hu.selester.seltransport.utils.AppUtils
import org.json.JSONArray
import org.json.JSONObject

class TransportListFragment : Fragment(), TransportListAdapter.RowClickListener,
    SimpleDialogFragment.ButtonListener {

    private lateinit var mBinding: FrgTransportListBinding
    private lateinit var mDb: SelTransportDatabase
    private var mTransportList: List<TransportsTable> = emptyList()
    private val mCallPhoneRequest = 104
    private lateinit var mChosenPhoneNumber: String
    private val mTag = "TransportListFragment"
    private lateinit var mLockDialog: SimpleDialogFragment

    override fun click(id: Long) {
        val bundle = bundleOf("transport_id" to id)
        findNavController().navigate(
            R.id.action_transportListFragment_to_addressListFragment,
            bundle
        )
    }

    override fun callPhone(phoneNumber: String) {
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

    override fun lockTransport(id: Long) {
        val mainText = getString(R.string.close_transport_confirm)
        mLockDialog = SimpleDialogFragment(
            this,
            resources.getString(R.string.lock_transport),
            mainText,
            resources.getString(R.string.cancel),
            resources.getString(R.string.lock)
        )
        mLockDialog.setTargetFragment(this, 3)
        mLockDialog.show(parentFragmentManager, mTag)
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val activity = requireActivity() as MainActivity
        activity.title = resources.getString(R.string.transports)
        activity.showTitleIcons()

        mBinding = FrgTransportListBinding.inflate(inflater)
        mBinding.transportListList.layoutManager = LinearLayoutManager(context)
        mBinding.transportListList.adapter = TransportListAdapter(
            requireContext(),
            mTransportList,
            this
        )

        loadData()

        return mBinding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDb = SelTransportDatabase.getInstance(requireContext())
        SessionClass.setValue("jwt_token", "")
    }

    inner class TransportDataGetter(
        override val mContext: Context,
        override var mRequestJson: JSONObject
    ) : AuthenticationHandler {

        override val mRequestUrl = resources.getString(R.string.root_url) + "/getTransportData.php"
        override val mTag = "TransportDataGetter"

        override fun onError() {
            mBinding.transportListProgressBar.visibility = View.GONE
            mBinding.transportListStatusMessage.text = ""
            AppUtils.toast(context, getString(R.string.communication_error))
        }

        override fun doAction(responseJson: JSONObject) {
            SessionClass.setValue(
                "jwt_token",
                responseJson.getJSONObject("newToken").getString("token")
            )
            try {
                val resultJson = responseJson.getJSONObject("result")
                val headJson = resultJson.getJSONObject("head")
                if (headJson.getString("taskType") != "TSKL") {
                    throw IllegalArgumentException("Task type (${headJson.getString("taskType")}) should be \"TSKL\"")
                }
                processData(resultJson.getJSONObject("body").getJSONArray("transports"))

            } catch (e: Exception) {
                Log.e(mTag, "Error: ${e.printStackTrace()}")
            }
        }
    }

    private fun loadData() {
        mBinding.transportListList.adapter = TransportListAdapter(
            requireContext(),
            emptyList(),
            this
        )
        mBinding.transportListProgressBar.visibility = View.VISIBLE
        mBinding.transportListStatusMessage.text = getString(R.string.data_loading)

        val headerJson = JSONObject()
        headerJson.put("taskType", "TRNSP")
        headerJson.put("interface", "1001")
        headerJson.put("sender", "droid")
        headerJson.put("recipient", "cp")

        val bodyJson = JSONObject()
        bodyJson.put("dummy", "dummy")

        val requestJson = JSONObject()
        requestJson.put("header", headerJson)
        requestJson.put("body", bodyJson)

        val dataLoader = TransportDataGetter(requireContext(), requestJson)
        dataLoader.doJob()
    }

    private fun processData(jsonData: JSONArray) {
        // TODO: this shouldn't be deleted
        mDb.actionDataFieldsDao().deleteAllData()
        mDb.addressesDao().deleteAllData()
        mDb.goodsDao().deleteAllData()
        mDb.logisticStatusesDao().deleteAllData()
        mDb.taskActionsDao().deleteAllData()
        mDb.transportsDao().deleteAllData()

        for (transportIt in 0 until jsonData.length()) {
            val transportJson = jsonData.getJSONObject(transportIt)

            mDb.transportsDao().insertTask(
                TransportsTable(
                    null,
                    transportJson.getLong("id"),
                    transportJson.getString("status"),
                    //transportJson.getInt("subscriberId"),
                    1,
                    transportJson.getString("subscriberName"),
                    transportJson.getString("transportNo"),
                    transportJson.getString("startDate"),
                    transportJson.getString("endDate"),
                    transportJson.getString("truckPlateNo"),
                    transportJson.getString("trailerPlateNo"),
                    transportJson.getJSONObject("contact").getString("name"),
                    transportJson.getJSONObject("contact").getString("phoneNumber"),
                    transportJson.getString("remarks")
                )
            )

            loadAddresses(transportJson.getJSONObject("addresses").getJSONArray("address"))
            loadActions(transportJson.getJSONObject("actions").getJSONArray("action"))
            loadGoods(transportJson.getJSONObject("goods").getJSONArray("item"))
            loadStatuses(
                transportJson.getJSONObject("statusTemplates").getJSONArray("statusTemplate")
            )
        }
        mTransportList = mDb.transportsDao().getAllData()
        loadList()
    }

    private fun loadStatuses(statusArray: JSONArray) {
        for (statusIt in 0 until statusArray.length()) {
            loadStatus(statusArray.getJSONObject(statusIt))
        }
    }

    private fun loadStatus(status: JSONObject) {
        mDb.logisticStatusesDao().insertStatus(
            LogisticStatusesTable(
                null,
                status.getLong("id"),
                status.getLong("transportId"),
                status.getInt("addressType"),
                status.getString("hu"),
                status.getString("de"),
                status.getString("en"),
                status.getString("pictureId")
            )
        )
    }

    private fun loadAddresses(addressArray: JSONArray) {
        for (addressIt in 0 until addressArray.length()) {
            loadAddress(addressArray.getJSONObject(addressIt))
        }
    }

    private fun loadAddress(address: JSONObject) {
        mDb.addressesDao().insertAddress(
            AddressesTable(
                null,
                address.getLong("id"),
                address.getLong("transportId"),
                address.getInt("type"),
                address.getString("description"),
                address.getString("date"),
                address.getString("name"),
                address.getString("country"),
                address.getString("zip"),
                address.getString("city"),
                address.getString("addr"),
                address.getJSONObject("coord").getDouble("lat"),
                address.getJSONObject("coord").getDouble("lon"),
                address.getJSONObject("contact").getString("name"),
                address.getJSONObject("contact").getString("phoneNumber"),
                address.getString("businessHours"),
                address.getString("remarks"),
                address.getString("infoField"),
                address.getLong("logisticStatusId")
            )
        )
    }

    private fun loadGoods(goodsArray: JSONArray) {
        for (goodsIt in 0 until goodsArray.length()) {
            loadGoodsItem(goodsArray.getJSONObject(goodsIt))
        }
    }

    private fun loadGoodsItem(goodsItem: JSONObject) {
        mDb.goodsDao().insertGoods(
            GoodsTable(
                null,
                goodsItem.getLong("id"),
                goodsItem.getLong("addressId"),
                goodsItem.getString("descr"),
                goodsItem.getString("descr2"),
                goodsItem.getInt("pcs"),
                goodsItem.getString("packaging"),
                goodsItem.getDouble("weight"),
                goodsItem.getDouble("space"),
                goodsItem.getDouble("volume"),
                goodsItem.getInt("sizeLength"),
                goodsItem.getInt("sizeWidth"),
                goodsItem.getInt("sizeHeight")
            )
        )
    }

    private fun loadActions(actionArray: JSONArray) {
        for (actionIt in 0 until actionArray.length()) {
            loadAction(
                actionArray.getJSONObject(actionIt),
                actionArray.getJSONObject(actionIt).getLong("addressId"),
                false
            )
        }
    }

    private fun loadActionDataFields(
        dataFields: JSONArray
    ) {
        for (fieldIt in 0 until dataFields.length()) {
            val dataField = dataFields.getJSONObject(fieldIt)
            mDb.actionDataFieldsDao().insertAddress(
                ActionDataFieldsTable(
                    null,
                    dataField.getLong("id"),
                    dataField.getLong("actionId"),
                    dataField.getString("description_hu"),
                    dataField.getString("description_en"),
                    dataField.getString("description_de"),
                    when (dataField.getString("dataType")) {
                        "integer" -> 1
                        "string" -> 2
                        "datetime" -> 3
                        else -> throw java.lang.IllegalArgumentException("Unknown data field type")
                    },
                    ""
                )
            )
        }
    }

    private fun loadAction(
        action: JSONObject,
        externalId: Long,
        isSubTask: Boolean
    ) {
        val isNested = action.getString("code") == "nested"
        if (!isNested) {
            mDb.taskActionsDao().insertAction(
                TaskActionsTable(
                    null,
                    action.getLong("id"),
                    externalId,
                    action.getString("name"),
                    action.getString("code"),
                    isSubTask
                )
            )
            val asd = action.get("dataFields")
            if (asd is JSONObject) {
                loadActionDataFields(action.getJSONObject("dataFields").getJSONArray("dataField"))
            }
        } else {
            val actionId = mDb.taskActionsDao().insertAction(
                TaskActionsTable(
                    null,
                    action.getLong("id"),
                    externalId,
                    action.getString("name"),
                    "nested",
                    isSubTask
                )
            )
            val nestedArray = action.getJSONObject("procedures").getJSONArray("action")
            for (nestedIt in 0 until nestedArray.length()) {
                loadAction(nestedArray.getJSONObject(nestedIt), actionId, true)
            }
        }
    }

    private fun loadList() {
        mBinding.transportListList.layoutManager = LinearLayoutManager(context)
        mBinding.transportListList.adapter = TransportListAdapter(
            requireContext(),
            mTransportList,
            this
        )

        mBinding.transportListProgressBar.visibility = View.GONE
        mBinding.transportListStatusMessage.text = ""
    }

    override fun onLeftClicked() {
        mLockDialog.dismiss()
    }

    override fun onRightClicked() {
        mLockDialog.dismiss()
    }
}
