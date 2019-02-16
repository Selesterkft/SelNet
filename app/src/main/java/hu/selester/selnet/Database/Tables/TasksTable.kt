package hu.selester.selnet.Database.Tables

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.io.Serializable

@Entity
data class TasksTable(
    @PrimaryKey(autoGenerate = true) var Id: Int?,
        var seqnum: Int,
        var orderId: Long,
        var ordId: Long,
        var ordLId: Long,
        var addressId: Long,
        var addressTypeId: Int,
        var company: String,
        var address: String,
        var shortInfo: String,
        var longInfo: String,
        var district: String,
        var city: String,
        var lat: Double,
        var lng: Double
): Serializable {
    constructor():this(null,0,0,0,0,0,0,"","","","", "","",0.0,0.0)

}