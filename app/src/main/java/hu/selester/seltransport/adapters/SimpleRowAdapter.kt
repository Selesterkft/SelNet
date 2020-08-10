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
import hu.selester.seltransport.database.tables.UsersTable
import hu.selester.seltransport.utils.AppUtils
import kotlinx.android.synthetic.main.row_simple.view.*

class SimpleRowAdapter(
    private val mContext: Context,
    private var mDataList: MutableList<Any>,
    private val mClick: RowClickListener
) : RecyclerView.Adapter<SimpleRowAdapter.BaseViewHolder<*>>() {

    private val mTag = "SimpleRowAdapter"

    companion object {
        const val TYPE_USER = 0
        const val TYPE_TASK_ACTION = 1
    }

    abstract inner class BaseViewHolder<T>(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        abstract fun bind(item: T, position: Int)
    }

    interface RowClickListener {
        fun simpleRowClick(id: Long, type: Int)
    }

    inner class UserViewHolder(view: View) : BaseViewHolder<UsersTable>(view) {
        private var mTelephoneNumber = view.row_simple_text
        private var mPicture = view.row_simple_picture
        private var rootView = itemView

        override fun bind(item: UsersTable, position: Int) {
            val phoneText = "+${item.telephoneNumber}"
            mTelephoneNumber.text = phoneText
            mPicture.setImageResource(R.drawable.mobile_phone)
            rootView.setOnClickListener { mClick.simpleRowClick(item.id!!, TYPE_USER) }
        }
    }

    inner class TaskActionViewHolder(view: View) : BaseViewHolder<TaskActionsTable>(view) {
        private var mText = view.row_simple_text
        private var mPicture = view.row_simple_picture
        private var rootView = itemView

        override fun bind(item: TaskActionsTable, position: Int) {
            mText.text = item.name

            when (item.code) {
                AppUtils.TRACKING -> {
                    mPicture.setImageResource(R.drawable.tracking)
                }
                AppUtils.MAP -> {
                    mPicture.setImageResource(R.drawable.poi)
                }
                AppUtils.GOODS -> {
                    mPicture.setImageResource(R.drawable.goods)
                }
                AppUtils.SIGNATURE -> {
                    mPicture.setImageResource(R.drawable.signature)
                }
                AppUtils.SCAN -> {
                    mPicture.setImageResource(R.drawable.camera)
                }
                AppUtils.SHOW_INFO -> {
                    mPicture.setImageResource(R.drawable.info)
                    val db = SelTransportDatabase.getInstance(mContext)
                    val address = db.addressesDao().getByCpDbId(item.externalId)
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
            rootView.setOnClickListener { mClick.simpleRowClick(item.id!!, TYPE_TASK_ACTION) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return when (viewType) {
            TYPE_USER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.row_simple, parent, false)
                UserViewHolder(view)
            }
            TYPE_TASK_ACTION -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.row_simple, parent, false)
                TaskActionViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid View type")
        }

    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        val element = mDataList[position]
        when (holder) {
            is UserViewHolder -> holder.bind(element as UsersTable, position)
            is TaskActionViewHolder -> holder.bind(element as TaskActionsTable, position)
        }
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (mDataList[position]) {
            is UsersTable -> TYPE_USER
            is TaskActionsTable -> TYPE_TASK_ACTION
            else -> throw IllegalArgumentException("Invalid data at $position")
        }
    }
}