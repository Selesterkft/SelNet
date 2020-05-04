package hu.selester.selnet.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import hu.selester.selnet.database.tables.TaskActionsTable

@Dao
interface TaskActionsDao {

    @Query("SELECT * FROM TaskActionsTable")
    fun getAllData(): List<TaskActionsTable>

    @Query("SELECT * FROM TaskActionsTable WHERE taskId =:taskId")
    fun getActionsForTask(taskId: Long): List<TaskActionsTable>

    @Query("SELECT * FROM TaskActionsTable WHERE Id =:id")
    fun getById(id: Long): List<TaskActionsTable>

    @Query("DELETE FROM TaskActionsTable")
    fun deleteAllData()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAction(data: TaskActionsTable): Long
}
