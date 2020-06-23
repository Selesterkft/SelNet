package hu.selester.seltransport.database.tables

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class SyncTasksTable(
    @PrimaryKey(autoGenerate = true) var id: Long?,
    var method: String, // POST, GET etc.
    var url: String,
    var jsonData: String
) : Serializable