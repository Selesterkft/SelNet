package hu.selester.seltransport.database.tables

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class LogisticStatusesTable(
    @PrimaryKey(autoGenerate = true) var id: Long?,
    var cpDbId: Long,
    var transportId: Long,
    var addressType: Int, // desc. @AddressesTable
    var hu: String,
    var de: String,
    var en: String,
    var pictureId: String
) : Serializable