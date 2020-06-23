package hu.selester.seltransport.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hu.selester.seltransport.R
import hu.selester.seltransport.database.tables.PhotosTable
import hu.selester.seltransport.utils.AppUtils
import kotlinx.android.synthetic.main.row_photo_list.view.*

class PhotosListAdapter(
    private var context: Context,
    private var dataList: MutableList<PhotosTable>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<PhotosListAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(id: Long, position: Int)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var date = view.row_photo_list_date!!
        var time = view.row_photo_list_time!!
        var image = view.row_photo_list_image!!
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.row_photo_list, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.date.text = dataList[position].date
        holder.time.text = dataList[position].time
        holder.image.setImageBitmap(AppUtils.loadLocalImage(dataList[position].filePath, 20))

        holder.image.setOnClickListener {
            listener.onItemClick(dataList[position].id!!, position)
        }
    }

    fun removeAt(position: Int) {
        dataList.removeAt(position)
        notifyDataSetChanged()
    }

    fun addItem(item: PhotosTable) {
        dataList.add(0, item)
        notifyDataSetChanged()
    }

    fun resetList(newList: MutableList<PhotosTable>) {
        dataList = newList
        notifyDataSetChanged()
    }
}