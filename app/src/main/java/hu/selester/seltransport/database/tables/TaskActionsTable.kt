package hu.selester.seltransport.database.tables

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class TaskActionsTable(
    @PrimaryKey(autoGenerate = true) var id: Long?,
    var cpDbId: Long,
    var externalId: Long, // foreign key (address cpDbId by default, but if nested then to parent actions "id")
    var name: String,
    var code: String, // see AppUtils for more info
    var isSubTask: Boolean // is a nested subtask of sth
) : Serializable
