package hu.selester.seltransport.Adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import hu.selester.seltransport.Database.Tables.OrderNums
import hu.selester.seltransport.R
import kotlinx.android.synthetic.main.row_transport_list.view.*

class TransportListAdapter(val context: Context, val dataList:MutableList<OrderNums>, val click: RowClickListener):RecyclerView.Adapter<TransportListAdapter.ViewHolder>(){


    interface RowClickListener {
        fun Click(orderId: String){}
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        var ordernum = view.row_transport_list_ordernum
        var rootView = view
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.row_transport_list, parent, false))
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.ordernum.text = dataList[position].orderNum
        viewHolder.rootView.setOnClickListener { click.Click(""+dataList[position].loginCode) }
    }
}