package hu.selester.seltransport.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hu.selester.seltransport.R
import hu.selester.seltransport.database.SelTransportDatabase
import hu.selester.seltransport.database.tables.AddressesTable
import hu.selester.seltransport.database.tables.TaskActionsTable
import hu.selester.seltransport.utils.AppUtils
import kotlinx.android.synthetic.main.row_address_list.view.*

class AddressListAdapter(
    private val mContext: Context,
    private var mDataList: List<AddressesTable>,
    private var mClickListener: AddressListClickListener
) : RecyclerView.Adapter<AddressListAdapter.BaseViewHolder>() {
    private val mDb = SelTransportDatabase.getInstance(mContext)

    companion object {
        const val NO_REMARKS = 0
        const val REMARKS = 1
    }

    interface AddressListClickListener {
        fun forwardClick(addressId: Long)
    }

    abstract inner class BaseViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        abstract fun bind(item: AddressesTable, position: Int)
    }


    inner class ViewHolderRemarks(view: View) : BaseViewHolder(view) {
        private val mCityZip = view.row_address_list_city_zip!!
        private val mAddress = view.row_address_list_address!!
        private val mDateBhr = view.row_address_list_date_business_hours!!
        private val mName = view.row_address_list_address_name!!
        private val mRemarks = view.row_address_list_remarks!!
        private val mLogisticStatus = view.row_address_list_logistic_status!!
        private val mLoadImage = view.row_address_list_load_image!!
        private val mCoverLayout = view.row_address_list_cover_layout!!

        override fun bind(item: AddressesTable, position: Int) {
            val cityZipStr = "${item.country}, ${item.zip}, ${item.city}"
            mCityZip.text = cityZipStr
            mAddress.text = item.address
            val dateBhrStr = "${AppUtils.formatDate(item.date)} ${item.businessHours}"
            mDateBhr.text = dateBhrStr
            mDateBhr.visibility = if (dateBhrStr == "") View.GONE else View.VISIBLE
            mName.text = item.name
            mRemarks.text = item.remarks

            val logisticStatusTable = mDb.logisticStatusesDao().getByCpId(item.logisticStatusId)
            val logisticStatusString =
                "${mContext.getString(R.string.logistic_status)}: ${AppUtils.getStatusName(
                    logisticStatusTable,
                    mContext
                )}"
            mLogisticStatus.text = logisticStatusString

            mRemarks.visibility = View.VISIBLE
            mLoadImage.setImageResource(if (item.type == 1) R.drawable.address_loading else R.drawable.address_unloading)

            mCoverLayout.setOnClickListener {
                mClickListener.forwardClick(item.id!!)
            }
        }
    }

    inner class ViewHolderNoRemarks(view: View) : BaseViewHolder(view) {
        private val mCityZip = view.row_address_list_city_zip!!
        private val mAddress = view.row_address_list_address!!
        private val mDateBhr = view.row_address_list_date_business_hours!!
        private val mName = view.row_address_list_address_name!!
        private val mRemarks = view.row_address_list_remarks!!
        private val mLogisticStatus = view.row_address_list_logistic_status!!
        private val mLoadImage = view.row_address_list_load_image!!
        private val mCoverLayout = view.row_address_list_cover_layout!!

        override fun bind(item: AddressesTable, position: Int) {
            val cityZipStr = "${item.country}, ${item.zip}, ${item.city}"
            mCityZip.text = cityZipStr
            mAddress.text = item.address
            val dateBhrStr = "${AppUtils.formatDate(item.date)} ${item.businessHours}"
            mDateBhr.text = dateBhrStr
            mDateBhr.visibility = if (dateBhrStr == "") View.GONE else View.VISIBLE
            mName.text = item.name
            mRemarks.text = item.remarks

            val logisticStatusTable = mDb.logisticStatusesDao().getByCpId(item.logisticStatusId)
            val logisticStatusString =
                "${mContext.getString(R.string.logistic_status)}: ${AppUtils.getStatusName(
                    logisticStatusTable,
                    mContext
                )}"
            mLogisticStatus.text = logisticStatusString

            mRemarks.visibility = View.GONE
            mLoadImage.setImageResource(if (item.type == 1) R.drawable.address_loading else R.drawable.address_unloading)

            mCoverLayout.setOnClickListener {
                mClickListener.forwardClick(item.id!!)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            NO_REMARKS -> {
                val view = LayoutInflater.from(mContext)
                    .inflate(R.layout.row_address_list, parent, false)
                ViewHolderNoRemarks(view)
            }
            REMARKS -> {
                val view = LayoutInflater.from(mContext)
                    .inflate(R.layout.row_address_list, parent, false)
                ViewHolderRemarks(view)
            }
            else -> throw IllegalArgumentException("Invalid View type")
        }
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    override fun onBindViewHolder(holder: AddressListAdapter.BaseViewHolder, position: Int) {
        val element = mDataList[position]
        when (holder) {
            is ViewHolderNoRemarks -> holder.bind(element, position)
            is ViewHolderRemarks -> holder.bind(element, position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (mDataList[position].remarks == "") {
            true -> NO_REMARKS
            false -> REMARKS
        }
    }
}