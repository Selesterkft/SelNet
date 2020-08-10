package hu.selester.seltransport.adapters

import android.content.Context
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.bold
import androidx.recyclerview.widget.RecyclerView
import hu.selester.seltransport.R
import hu.selester.seltransport.database.tables.GoodsTable
import kotlinx.android.synthetic.main.row_goods.view.*

class GoodsAdapter(
    val mContext: Context,
    private var mDataList: List<GoodsTable>
) : RecyclerView.Adapter<GoodsAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val mDescription = itemView.row_goods_description
        private val mDescription2 = itemView.row_goods_description2
        private val mAmount = itemView.row_goods_amount
        private val mPackaging = itemView.row_goods_unit
        private val mWeight = itemView.row_goods_weight
        private val mSpace = itemView.row_goods_space
        private val mVolume = itemView.row_goods_volume
        private val mDimensions = itemView.row_goods_dimensions

        fun bind(item: GoodsTable) {
            val description = SpannableStringBuilder().append(mContext.getString(R.string.id_number))
                .bold { append(item.description) }
            mDescription.text = description
            val description2 = SpannableStringBuilder()
                .append(mContext.getString(R.string.description))
                .bold { append(item.description2) }
            mDescription2.text = description2
            val amount = SpannableStringBuilder()
                .append(mContext.getString(R.string.amount))
                .bold { append("${item.amount}") }
            mAmount.text = amount
            val packaging = SpannableStringBuilder()
                .append(mContext.getString(R.string.packaging))
                .bold { append(item.packaging) }
            mPackaging.text = packaging
            val weight = SpannableStringBuilder()
                .append(mContext.getString(R.string.weight))
                .bold { append("${item.weight}kg") }
            mWeight.text = weight
            val space = SpannableStringBuilder()
                .append(mContext.getString(R.string.space))
                .bold { append("${item.space}LM") }
            mSpace.text = space
            val volume = SpannableStringBuilder()
                .append(mContext.getString(R.string.volume))
                .bold { append("${item.volume}m3") }
            mVolume.text = volume
            val dimensions = SpannableStringBuilder()
                .append(mContext.getString(R.string.dimensions))
                .bold { append("${item.sizeLength}x${item.sizeWidth}x${item.sizeHeight}") }
                .append(mContext.getString(R.string.l_w_h))
            mDimensions.text = dimensions
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_goods, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }


    override fun onBindViewHolder(holder: GoodsAdapter.ViewHolder, position: Int) {
        val element = mDataList[position]
        holder.bind(element)
    }
}