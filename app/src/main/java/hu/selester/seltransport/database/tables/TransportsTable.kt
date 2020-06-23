package hu.selester.seltransport.database.tables

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class TransportsTable(
    @PrimaryKey(autoGenerate = true) var id: Long?,
    var cpDbId: Long,
    var status: String,
    var subscriberId: Int,
    var subscriberName: String,
    var transportNo: String,
    var startDate: String,
    var endDate: String,
    var truckPlateNo: String,
    var trailerPlateNo: String,
    var contactName: String,
    var contactPhoneNumber: String,
    var remarks: String
) : Serializable
