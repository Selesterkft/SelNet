package hu.selester.seltransport.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import hu.selester.seltransport.database.tables.TaskStatusTable

@Dao
interface TaskStatusDao {
    @Query("SELECT * FROM TaskStatusTable WHERE transportId =:transportId")
    fun getByTaskId(transportId: Long): List<TaskStatusTable>

    @Query("DELETE FROM TaskStatusTable")
    fun deleteAllData()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStatus(data: TaskStatusTable): Long
}