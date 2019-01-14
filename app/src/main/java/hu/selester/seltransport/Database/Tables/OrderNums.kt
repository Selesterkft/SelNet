package hu.selester.seltransport.Database.Tables

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class OrderNums(
    @PrimaryKey(autoGenerate = false) var Id: Int?,
    var loginCode: String,
    var orderNum: String){

    constructor():this(null,"","")

}