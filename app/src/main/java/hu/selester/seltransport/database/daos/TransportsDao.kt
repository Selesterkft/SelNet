package hu.selester.seltransport.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import hu.selester.seltransport.database.tables.TransportsTable

@Dao
interface TransportsDao {
    @Query("SELECT * FROM TransportsTable WHERE id = :id")
    fun getData(id: Long): List<TransportsTable>

    @Query("SELECT * FROM TransportsTable")
    fun getAllData(): List<TransportsTable>

    @Query("SELECT COUNT(id) FROM TransportsTable")
    fun getCount(): Int

    @Query("DELETE FROM TransportsTable WHERE id > 0")
    fun deleteAllData()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTask(data: TransportsTable): Long
}
