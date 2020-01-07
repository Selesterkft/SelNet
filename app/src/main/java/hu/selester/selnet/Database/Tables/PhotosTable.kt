package hu.selester.selnet.Database.Tables

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PhotosTable(
    @PrimaryKey(autoGenerate = true) var id: Long?,
    var orderId: Int,
    var addrId: Int,
    var ptype: Int,
    var ptypeText: String,
    var datetime: String,
    var filePath: String,
    var uploaded: Int,
    var tried: Int
    ){
    constructor():this(null,0,0,0,"","","",0,0)
}
