package hu.selester.seltransport.Adapters

import android.content.Context
import android.graphics.BitmapFactory
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.OrientationEventListener
import android.view.View
import android.view.ViewGroup
import hu.selester.seltransport.Database.Tables.PhotosTable
import hu.selester.seltransport.Helper.HelperClass
import hu.selester.seltransport.R
import kotlinx.android.synthetic.main.row_photos_list.view.*

class PhotosListAdapter(private var context: Context, val dataList: MutableList<PhotosTable>, val listener: OnItemClickListener): RecyclerView.Adapter<PhotosListAdapter.ViewHolder>(){

    interface OnItemClickListener {
        fun onItemClick( item: String)
        fun onDelQuestion( item: Int)
    }

    class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        var name = view.photos_list_text
        var image = view.photos_list_image
        var datetime = view.photos_list_date
        var delBtn = view.photos_delBin
        var showLayer = view.photos_textLayout
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.row_photos_list, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = dataList[position].ptypeText
        holder.datetime.text = dataList[position].datetime
        holder.image.setImageBitmap(HelperClass.loadLocalImage(dataList[position].filePath,20) )
        holder.delBtn.setOnClickListener { listener.onDelQuestion (position) }
        holder.image.setOnClickListener {
            listener.onItemClick(dataList[position].filePath)
        }
        holder.showLayer.setOnClickListener {
            listener.onItemClick(dataList[position].filePath)
        }
    }

    fun removeAt(position: Int) {
        dataList.removeAt(position)
        notifyDataSetChanged()
    }

    fun addItem(item: PhotosTable){
        dataList.add(item)
        notifyDataSetChanged()
    }
}