package hu.selester.seltransport.Database.Tables

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import java.io.Serializable

@Entity
data class TransportDatasTable (
    @PrimaryKey(autoGenerate = true) var id: Long?,
    var loginCode: String,
    var seqNum: Int,
    var addressType: Int,
    var addressID: Int,
    var expire: String,
    var custID: Int,
    var name: String,
    var district: String,
    var city: String,
    var address: String,
    var lat: Double,
    var lng: Double):Serializable{

    constructor():this(null,"",0,0,0,"",0,"","","","", 0.0,0.0)
}