package hu.selester.seltransport.database.tables

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class PicturesTable(
    @PrimaryKey(autoGenerate = true) var id: Long?,
    var addressId: Long,
    var date: String,
    var time: String,
    var filePath: String,
    var uploaded: Boolean,
    var tried: Int
) : Serializable

