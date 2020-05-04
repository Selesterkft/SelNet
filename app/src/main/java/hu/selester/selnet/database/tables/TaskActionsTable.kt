package hu.selester.selnet.database.tables

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TaskActionsTable(
    @PrimaryKey(autoGenerate = true) var Id: Long?,
    var TaskId: Long, // foreign key
    var ListId: Int, // list index
    var IsSubTask: Boolean, // is a nested subtask of sth
    var Name: String,
    var Procedure: String // eg. "Info", "Signature", "Nested" etc.
)
