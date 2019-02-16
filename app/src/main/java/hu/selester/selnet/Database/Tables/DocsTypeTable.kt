package hu.selester.selnet.Database.Tables

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class DocsTypeTable(
    @PrimaryKey(autoGenerate = true) var id: Int?,
    var docId: Int,
    var docName: String,
    var transactId: Int
    ){
    constructor():this(null,0,"",0)
}