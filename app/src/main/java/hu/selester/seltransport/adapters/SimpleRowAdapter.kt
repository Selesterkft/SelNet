package hu.selester.seltransport.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hu.selester.seltransport.R
import hu.selester.seltransport.database.tables.TaskActionsTable
import hu.selester.seltransport.database.tables.UsersTable
import kotlinx.android.synthetic.main.row_simple.view.*

class SimpleRowAdapter(
    val context: Context,
    private var dataList: List<Any>,
    private val click: RowClickListener
) : RecyclerView.Adapter<SimpleRowAdapter.BaseViewHolder<*>>() {

    private val mTag = "SimpleRowAdapter"

    companion object {
        const val TYPE_USER = 0
        const val TYPE_TASK_ACTION = 1
    }

    abstract class BaseViewHolder<T>(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        abstract fun bind(item: T)
    }

    interface RowClickListener {
        fun simpleRowClick(id: Long, type: Int)
    }

    inner class UserViewHolder(view: View) : BaseViewHolder<UsersTable>(view) {
        private var mTelephoneNumber = view.row_simple_text
        private var mPicture = view.row_simple_picture
        private var rootView = itemView

        override fun bind(item: UsersTable) {
            mTelephoneNumber.text = item.telephoneNumber
            mPicture.setImageResource(R.drawable.mobile_phone)
            rootView.setOnClickListener { click.simpleRowClick(item.id!!, TYPE_USER) }
        }
    }

    inner class TaskActionViewHolder(view: View) : BaseViewHolder<TaskActionsTable>(view) {
        private var mText = view.row_simple_text
        private var mPicture = view.row_simple_picture
        private var rootView = itemView

        override fun bind(item: TaskActionsTable) {
            mText.text = item.name
            mPicture.setImageResource(
                when (item.code) {
                    "tracking" -> R.drawable.tracking
                    "map" -> R.drawable.poi
                    "goods" -> R.drawable.goods
                    "signature" -> R.drawable.signature
                    "scan" -> R.drawable.camera
                    "show_info" -> R.drawable.info
                    "get_data" -> R.drawable.get_data
                    else -> {
                        Log.e(mTag, "Unknown task code")
                        R.drawable.icon_info
                    }
                }
            )
            rootView.setOnClickListener { click.simpleRowClick(item.id!!, TYPE_TASK_ACTION) }
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
        val element = dataList[position]
        when (holder) {
            is UserViewHolder -> holder.bind(element as UsersTable)
            is TaskActionViewHolder -> holder.bind(element as TaskActionsTable)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (dataList[position]) {
            is UsersTable -> TYPE_USER
            is TaskActionsTable -> TYPE_TASK_ACTION
            else -> throw IllegalArgumentException("Invalid data at $position")
        }
    }
}