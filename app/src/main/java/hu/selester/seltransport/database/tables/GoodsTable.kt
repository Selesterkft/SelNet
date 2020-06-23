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
    var weight: Int,
    var space: Int,
    var volume: Int,
    var sizeLength: Int,
    var sizeWidth: Int,
    var sizeHeight: Int
) : Serializable