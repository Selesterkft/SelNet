package hu.selester.selnet.Database.Tables

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class CompaniesTables(
    @PrimaryKey var id: Long?,
    var companyCode: String,
    var orderId: Long,
    var orderNumber: String
){

    constructor():this(null,"",0,"")
}