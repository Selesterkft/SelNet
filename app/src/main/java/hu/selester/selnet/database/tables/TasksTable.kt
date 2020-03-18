package hu.selester.selnet.database.tables

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class TasksTable(
    @PrimaryKey(autoGenerate = true) var Id: Int?,
    var orderId: Long,
    var company: String,
    var shortInfo: String,
    var longInfo: String,
    var address: String,
    var zip: String,
    var city: String,
    var lat: Double,
    var lng: Double
) : Serializable
