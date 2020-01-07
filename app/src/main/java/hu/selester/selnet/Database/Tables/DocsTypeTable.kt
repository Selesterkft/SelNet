package hu.selester.selnet.Database.Tables

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DocsTypeTable(
    @PrimaryKey(autoGenerate = true) var id: Int?,
    var docId: Int,
    var docName: String,
    var transactId: Int
    ){
    constructor():this(null,0,"",0)
}