package hu.selester.selnet.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hu.selester.selnet.R
import hu.selester.selnet.database.tables.TaskActionsTable
import hu.selester.selnet.database.tables.TasksTable
import kotlinx.android.synthetic.main.row_transport_list.view.*
import java.lang.IllegalArgumentException


class TransportListAdapter(
    val context: Context,
    private var dataList: List<Any>,
    private val click: RowClickListener
) : RecyclerView.Adapter<TransportListAdapter.TaskBaseViewHolder<*>>() {

    abstract class TaskBaseViewHolder<T>(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        abstract fun bind(item: T)
    }

    companion object {
        const val TYPE_TASK = 0
        const val TYPE_ACTION = 1
    }

    interface RowClickListener {
        fun click(id: Long, type: Int) {}
    }

    inner class TaskViewHolder(itemView: View) :
        TaskBaseViewHolder<TasksTable>(itemView) {
        var companyName = itemView.row_transport_list_main_text
        var rootView = itemView

        override fun bind(item: TasksTable) {
            companyName.text = item.company
            rootView.setOnClickListener { click.click(item.Id!!, TYPE_TASK) }
        }
    }

    inner class ActionViewHolder(itemView: View) :
        TaskBaseViewHolder<TaskActionsTable>(itemView) {
        var actionName = itemView.row_transport_list_main_text
        var rootView = itemView

        override fun bind(item: TaskActionsTable) {
            actionName.text = item.Name
            rootView.setOnClickListener { click.click(item.Id!!, TYPE_ACTION) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskBaseViewHolder<*> {
        return when (viewType) {
            TYPE_TASK -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.row_transport_list, parent, false)
                TaskViewHolder(view)
            }
            TYPE_ACTION -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.row_transport_list, parent, false)
                ActionViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid View type")
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(viewHolder: TaskBaseViewHolder<*>, position: Int) {
        val element = dataList[position]
        when(viewHolder) {
            is TaskViewHolder -> viewHolder.bind(element as TasksTable)
            is ActionViewHolder -> viewHolder.bind(element as TaskActionsTable)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (dataList[position]) {
            is TasksTable -> TYPE_TASK
            is TaskActionsTable -> TYPE_ACTION
            else -> throw IllegalArgumentException("Invalid data at $position")
        }
    }
}
