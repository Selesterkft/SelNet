package hu.selester.seltransport.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hu.selester.seltransport.R
import hu.selester.seltransport.database.tables.TransportsTable
import hu.selester.seltransport.utils.AppUtils
import kotlinx.android.synthetic.main.row_transport_list.view.*


class TransportListAdapter(
    val mContext: Context,
    private var mDataList: List<TransportsTable>,
    private val mClickListener: RowClickListener
) : RecyclerView.Adapter<TransportListAdapter.TransportViewHolder>() {

    interface RowClickListener {
        fun click(id: Long)
        fun callPhone(phoneNumber: String)
        fun lockTransport(id: Long)
    }

    inner class TransportViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val mCoverLayout = itemView.row_transport_list_cover_layout!!
        private val mTransportNumber = itemView.row_transport_transport_no!!
        private val mSubscriber = itemView.row_transport_list_subscriber!!
        private val mDate = itemView.row_transport_list_date!!
        private val mContactName = itemView.row_transport_list_contact_name!!
        private val mContactPhone = itemView.row_transport_list_contact_phone!!
        private val mRemarks = itemView.row_transport_list_remarks!!
        private val mTruckPlateNo = itemView.row_transport_list_truck_plate!!
        private val mTrailerNo = itemView.row_transport_list_trailer_plate!!
        private val mContactLayout = itemView.row_transport_list_contact_layout!!
        private val mLockLayout = itemView.row_transport_list_lock_layout

        fun bind(item: TransportsTable) {
            mTransportNumber.text = item.transportNo
            mSubscriber.text = item.subscriberName
            val dateString =
                "${AppUtils.formatDate(item.startDate)} - ${AppUtils.formatDate(item.endDate)}"
            mDate.text = dateString
            mContactName.text = item.contactName
            mContactPhone.text = item.contactPhoneNumber
            mTruckPlateNo.text = item.truckPlateNo
            mTrailerNo.text = item.trailerPlateNo
            mRemarks.text = item.remarks
            mCoverLayout.setOnClickListener { mClickListener.click(item.cpDbId) }
            if (item.contactName == "" && item.contactPhoneNumber == "") {
                mContactLayout.visibility = View.GONE
            } else {
                mContactLayout.setOnClickListener {
                    mClickListener.callPhone(item.contactPhoneNumber)
                }
            }
            mLockLayout.setOnClickListener {
                mClickListener.lockTransport(item.id!!)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransportViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_transport_list, parent, false)
        return TransportViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    override fun onBindViewHolder(viewHolder: TransportViewHolder, position: Int) {
        val element = mDataList[position]
        viewHolder.bind(element)
    }
}
