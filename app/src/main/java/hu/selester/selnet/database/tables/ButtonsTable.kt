package hu.selester.selnet.database.tables

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ButtonsTable(
    @PrimaryKey var id: Int?,
    var objectType: String,
    var childDataLabel: String,
    var childData: String,
    var objectFormat: String,
    var mandatory: String,
    var remark: String
)
