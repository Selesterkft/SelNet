package hu.selester.seltransport.database.tables

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class TaskStatusTable(
    @PrimaryKey(autoGenerate = true) var Id: Long?,
    var transportId: Long, // foreign key
    var externalId: Int,
    var type: Int, // 1: load, 2: unload
    var huName: String,
    var deName: String,
    var enName: String,
    var pictureName: String
) : Serializable