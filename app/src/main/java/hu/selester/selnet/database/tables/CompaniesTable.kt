package hu.selester.selnet.database.tables

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class CompaniesTable(
    @PrimaryKey var id: Long?,
    var companyCode: String,
    var orderId: Long,
    var orderNumber: String
)
