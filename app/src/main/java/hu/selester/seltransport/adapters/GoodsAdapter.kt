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
            val description = SpannableStringBuilder().append("Azonosító szám: ")
                .bold { append(item.description) }
            mDescription.text = description
            val description2 = SpannableStringBuilder()
                .append("Megnevezés: ")
                .bold { append(item.description2) }
            mDescription2.text = description2
            val amount = SpannableStringBuilder()
                .append("Darab: ")
                .bold { append("${item.amount}") }
            mAmount.text = amount
            val packaging = SpannableStringBuilder()
                .append("Csomagolás: ")
                .bold { append(item.packaging) }
            mPackaging.text = packaging
            val weight = SpannableStringBuilder()
                .append("Súly: ")
                .bold { append("${item.weight}kg") }
            mWeight.text = weight
            val space = SpannableStringBuilder()
                .append("Rakhossz: ")
                .bold { append("${item.space}LM") }
            mSpace.text = space
            val volume = SpannableStringBuilder()
                .append("Térfogat: ")
                .bold { append("${item.volume}m3") }
            mVolume.text = volume
            val dimensions = SpannableStringBuilder()
                .append("Dimenzió: ")
                .bold { append("${item.sizeLength}x${item.sizeWidth}x${item.sizeHeight}") }
                .append(" (HxSzxM)")
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