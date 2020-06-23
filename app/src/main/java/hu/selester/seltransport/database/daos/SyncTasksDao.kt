package hu.selester.seltransport.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import hu.selester.seltransport.database.tables.SyncTasksTable

@Dao
interface SyncTasksDao {
    @Query("DELETE FROM SyncTasksTable WHERE id > 0")
    fun deleteAllData()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTask(task: SyncTasksTable): Long
}