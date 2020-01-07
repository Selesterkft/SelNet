package hu.selester.selnet.Database.Tables

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class ButtonsTable(
    @PrimaryKey var id: Int?,
    var objectType: String,
    var childDataLabel: String,
    var childData: String,
    var objectFormat: String,
    var mandatory: String,
    var remark: String
){
    constructor():this(0,"","","","","","")
}