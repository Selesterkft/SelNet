package hu.selester.seltransport.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hu.selester.seltransport.R
import hu.selester.seltransport.database.SelTransportDatabase
import hu.selester.seltransport.database.tables.TaskActionsTable
import hu.selester.seltransport.utils.AppUtils
import hu.selester.seltransport.utils.AppUtils.Companion.format
import kotlinx.android.synthetic.main.row_double.view.*

class DoubleRowAdapter(
    private val mContext: Context,
    private var mDataList: MutableList<Any>,
    private val mClick: RowClickListener
) : RecyclerView.Adapter<DoubleRowAdapter.BaseViewHolder<*>>() {

    private val mTag = "DoubleRowAdapter"

    companion object {
        const val TYPE_TASK_ACTION = 0
    }

    abstract inner class BaseViewHolder<T>(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        abstract fun bind(item: T, position: Int)
    }

    interface RowClickListener {
        fun doubleRowClick(id: Long, type: Int)
    }

    inner class TaskActionViewHolder(view: View) : BaseViewHolder<TaskActionsTable>(view) {
        private var mFirstRow = view.row_double_first_line
        private var mSecondLine = view.row_double_second_line
        private var mPicture = view.row_double_picture
        private var rootView = itemView

        override fun bind(item: TaskActionsTable, position: Int) {
            mFirstRow.text = AppUtils.getActionName(item, mContext)
            val db = SelTransportDatabase.getInstance(mContext)
            val address = db.addressesDao().getByCpDbId(item.externalId)

            when (item.code) {
                AppUtils.TRACKING -> {
                    mPicture.setImageResource(R.drawable.tracking)
                    val currentStatus =
                        db.logisticStatusesDao().getByCpId(address.logisticStatusId)
                    val nextStatuses =
                        db.logisticStatusesDao()
                            .getNextStatuses(address.transportId, address.type, currentStatus.id!!)
                    val secondRow = if (nextStatuses.isEmpty()) {
                        mContext.getString(
                            R.string.last_status,
                            AppUtils.getStatusName(currentStatus, mContext)
                        )
                    } else {
                        "${AppUtils.getStatusName(
                            currentStatus,
                            mContext
                        )} -> ${AppUtils.getStatusName(nextStatuses[0], mContext)}"
                    }
                    mSecondLine.text = secondRow
                }
                AppUtils.MAP -> {
                    mPicture.setImageResource(R.drawable.poi)
                    mSecondLine.text = mContext.getString(R.string.open_map)
                }
                AppUtils.GOODS -> {
                    mPicture.setImageResource(R.drawable.goods)
                    val goods = db.goodsDao().getByAddressCpDbId(address.cpDbId)
                    var secondLine = ""
                    mSecondLine.text = if (goods.isNotEmpty()) {
                        secondLine += "${goods.size} colli"
                        val weight = db.goodsDao().getSumWeightByAddressCpDbId(address.cpDbId)
                        if (weight != 0) secondLine += ", $weight kg"
                        val volume = db.goodsDao().getSumVolumeByAddressCpDbId(address.cpDbId)
                        if (volume != 0.0) secondLine += ", ${volume.format(2)}"
                        val space = db.goodsDao().getSumSpaceByAddressCpDbId(address.cpDbId)
                        if (space != 0.0) secondLine += ", ${space.format(2)}"
                        secondLine
                    } else {
                        ""
                    }

                    mContext.getString(
                        R.string.goods_statistics,
                        goods.size,
                        db.goodsDao().getSumWeightByAddressCpDbId(address.cpDbId),
                        db.goodsDao().getSumVolumeByAddressCpDbId(address.cpDbId),
                        db.goodsDao().getSumSpaceByAddressCpDbId(address.cpDbId)
                    )
                    mSecondLine.text = secondLine
                }
                AppUtils.SIGNATURE -> {
                    mPicture.setImageResource(R.drawable.signature)
                    val signatures = db.signaturesDao().getByAddressId(address.id!!)
                    val secondLine = mContext.getString(
                        if (signatures.isEmpty()) {
                            R.string.unsigned
                        } else {
                            R.string.signed
                        }
                    )
                    mSecondLine.text = secondLine
                }
                AppUtils.SCAN -> {
                    mPicture.setImageResource(R.drawable.camera)
                    val pictures = db.picturesDao().getPhotosForAddress(address.id!!)
                    val secondLine = mContext.getString(R.string.n_pictures_taken, pictures.size)
                    mSecondLine.text = secondLine
                }
                AppUtils.SHOW_INFO -> {
                    mPicture.setImageResource(R.drawable.info)
                    mSecondLine.text = mContext.getString(R.string.more_info)
                    if (address.infoField == "") {
                        mDataList.removeAt(position)
                        notifyDataSetChanged()
                    }
                }
                AppUtils.GET_DATA -> {
                    mPicture.setImageResource(R.drawable.get_data)
                }
                else -> {
                    Log.e(mTag, "Unknown task code")
                    mPicture.setImageResource(R.drawable.icon_info)
                }
            }
            rootView.setOnClickListener {
                mClick.doubleRowClick(
                    item.id!!,
                    SimpleRowAdapter.TYPE_TASK_ACTION
                )
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<*> {
        return when (viewType) {
            TYPE_TASK_ACTION -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.row_double, parent, false)
                TaskActionViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid View type")
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        val element = mDataList[position]
        when (holder) {
            is TaskActionViewHolder -> holder.bind(element as TaskActionsTable, position)
        }
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (mDataList[position]) {
            is TaskActionsTable -> TYPE_TASK_ACTION
            else -> throw IllegalArgumentException("Invalid data at $position")
        }
    }
}