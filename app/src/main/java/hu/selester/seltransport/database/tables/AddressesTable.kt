package hu.selester.seltransport.database.tables

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class AddressesTable(
    @PrimaryKey(autoGenerate = true) var id: Long?,
    var cpDbId: Long,
    var transportId: Long, // id of the transport which this belongs to
    var type: Int, // 1: load, 2: unload
    var description: String,
    var date: String,
    var name: String,
    var country: String,
    var city: String,
    var zip: String,
    var address: String,
    var lat: Double,
    var lon: Double,
    var contactName: String,
    var contactPhoneNumber: String,
    var businessHours: String,
    var remarks: String,
    var infoField: String,
    var logisticStatusId: Long
) : Serializable
