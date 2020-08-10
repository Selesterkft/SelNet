package hu.selester.seltransport.database.tables

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class ActionDataFieldsTable(
    @PrimaryKey(autoGenerate = true) var id: Long?,
    var cpDbId: Long,
    var actionId: Long,
    var hu: String,
    var en: String,
    var de: String,
    var type: Int, // 1: Integer, 2: String, 3: Datetime
    var value: String
) : Serializable