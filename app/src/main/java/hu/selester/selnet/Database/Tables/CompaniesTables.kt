package hu.selester.selnet.Database.Tables

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
class CompaniesTables(
    @PrimaryKey var id: Long?,
    var companyCode: String,
    var orderId: Long,
    var orderNumber: String
){

    constructor():this(null,"",0,"")
}