package hu.selester.seltransport.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import hu.selester.seltransport.database.tables.TaskActionsTable

@Dao
interface TaskActionsDao {

    @Query("SELECT * FROM TaskActionsTable")
    fun getAllData(): List<TaskActionsTable>

    @Query("SELECT * FROM TaskActionsTable WHERE externalId =:taskId")
    fun getByExtId(taskId: Long): List<TaskActionsTable>

    @Query("SELECT * FROM TaskActionsTable WHERE id =:id")
    fun getById(id: Long): TaskActionsTable

    @Query("DELETE FROM TaskActionsTable")
    fun deleteAllData()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAction(data: TaskActionsTable): Long
}
