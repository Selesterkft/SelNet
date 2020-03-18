package hu.selester.selnet.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import hu.selester.selnet.database.tables.CompaniesTable
import hu.selester.selnet.R
import kotlinx.android.synthetic.main.row_transport_list.view.*

class TransportListAdapter(
    val context: Context,
    private val dataList: MutableList<CompaniesTable>,
    private val click: RowClickListener
) : RecyclerView.Adapter<TransportListAdapter.ViewHolder>() {


    interface RowClickListener {
        fun click(orderId: String) {}
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var companyCode = view.row_transport_list_main_text
        var rootView = view
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.row_transport_list, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.companyCode.text = dataList[position].companyCode
        viewHolder.rootView.setOnClickListener { click.click("" + dataList[position].orderId) }
    }
}