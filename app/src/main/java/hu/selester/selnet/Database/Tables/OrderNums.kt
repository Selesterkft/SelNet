package hu.selester.selnet.Database.Tables

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class OrderNums(
    @PrimaryKey(autoGenerate = false) var Id: Int?,
    var loginCode: String,
    var orderNum: String){

    constructor():this(null,"","")

}