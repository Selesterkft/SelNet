package hu.selester.selnet.database.tables

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SignaturesTable(
    @PrimaryKey(autoGenerate = false) var id: Int?

)
