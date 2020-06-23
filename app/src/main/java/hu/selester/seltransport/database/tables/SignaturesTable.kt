package hu.selester.seltransport.database.tables

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class SignaturesTable(
    @PrimaryKey(autoGenerate = false) var id: Long?
) : Serializable
