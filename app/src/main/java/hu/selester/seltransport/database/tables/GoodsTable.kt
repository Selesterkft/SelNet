package hu.selester.seltransport.database.tables

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class GoodsTable(
    @PrimaryKey(autoGenerate = true) var id: Long?,
    var cpDbId: Long,
    var addressId: Long,
    var description: String,
    var description2: String,
    var amount: Int,
    var packaging: String,
    var weight: Double,
    var space: Double,
    var volume: Double,
    var sizeLength: Int,
    var sizeWidth: Int,
    var sizeHeight: Int
) : Serializable