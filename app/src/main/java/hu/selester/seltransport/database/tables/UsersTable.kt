package hu.selester.seltransport.database.tables

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
class UsersTable(
    @PrimaryKey(autoGenerate = true) var id: Long?,
    var telephoneNumber: String,
    var masterKey: String?, // Rest API
    var registrationKey: String? // SMS
) : Serializable