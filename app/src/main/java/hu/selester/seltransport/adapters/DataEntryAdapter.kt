package hu.selester.seltransport.adapters

import android.app.DatePickerDialog
import android.content.Context
import android.os.Build
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hu.selester.seltransport.R
import hu.selester.seltransport.database.tables.ActionDataFieldsTable
import hu.selester.seltransport.utils.AppUtils
import kotlinx.android.synthetic.main.row_action_data_field.view.*
import java.text.SimpleDateFormat
import java.util.*


class DataEntryAdapter(
    private val mContext: Context,
    private val mDataList: List<ActionDataFieldsTable>,
    private val mListener: RowSendListener
) : RecyclerView.Adapter<DataEntryAdapter.BaseViewHolder>() {
    private val mTag = "ActionDataFieldAdapter"

    companion object {
        const val TYPE_INTEGER = 1
        const val TYPE_STRING = 2
        const val TYPE_DATETIME = 3
    }

    interface RowSendListener {

    }

    abstract inner class BaseViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        abstract fun bind(item: ActionDataFieldsTable, position: Int)
    }

    inner class IntegerViewHolder(view: View) : BaseViewHolder(view) {
        private val mTextInputLayout = view.row_action_data_field_text

        override fun bind(item: ActionDataFieldsTable, position: Int) {
            mTextInputLayout.hint = AppUtils.getActionDataFieldName(item, mContext)
            mTextInputLayout.editText!!.setRawInputType(InputType.TYPE_CLASS_NUMBER)
        }
    }

    inner class StringViewHolder(view: View) : BaseViewHolder(view) {
        private val mTextInputLayout = view.row_action_data_field_text

        override fun bind(item: ActionDataFieldsTable, position: Int) {
            mTextInputLayout.hint = AppUtils.getActionDataFieldName(item, mContext)
        }
    }


    inner class DateTimeViewHolder(view: View) : BaseViewHolder(view) {
        private val mTextInputLayout = view.row_action_data_field_text
        private val mCalendar = Calendar.getInstance()

        override fun bind(item: ActionDataFieldsTable, position: Int) {
            val date =
                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    mCalendar.set(Calendar.YEAR, year)
                    mCalendar.set(Calendar.MONTH, monthOfYear)
                    mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    updateLabel()
                }

            mTextInputLayout.editText!!.setOnClickListener {
                DatePickerDialog(
                    mContext,
                    date,
                    mCalendar.get(Calendar.YEAR),
                    mCalendar.get(Calendar.MONTH),
                    mCalendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
            mTextInputLayout.hint = AppUtils.getActionDataFieldName(item, mContext)
            mTextInputLayout.isFocusable = false
            mTextInputLayout.editText!!.isFocusable = false
        }

        private fun updateLabel() {
            val format = "MM/dd/yy" //In which you need put here
            val sdf = SimpleDateFormat(format, Locale.US)
            mTextInputLayout.editText!!.setText(sdf.format(mCalendar.time))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            TYPE_INTEGER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.row_action_data_field, parent, false)
                IntegerViewHolder(view)
            }
            TYPE_STRING -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.row_action_data_field, parent, false)
                StringViewHolder(view)
            }
            TYPE_DATETIME -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.row_action_data_field, parent, false)
                DateTimeViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid View type")
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val element = mDataList[position]
        holder.bind(element, position)
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (mDataList[position].type) {
            1 -> TYPE_INTEGER
            2 -> TYPE_STRING
            3 -> TYPE_DATETIME
            else -> throw IllegalArgumentException("Invalid data at $position")
        }
    }
}