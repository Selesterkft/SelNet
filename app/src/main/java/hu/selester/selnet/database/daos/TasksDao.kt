package hu.selester.selnet.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import hu.selester.selnet.database.tables.TasksTable

@Dao
interface TasksDao{

    /* @Query("SELECT * FROM TasksTable WHERE orderId = :orderID AND addressId = :addressId")
    fun getAddressData(orderID: Long, addressId: Long): TasksTable */

    @Query("SELECT * FROM TasksTable WHERE orderId = :orderID")
    fun getOrderData(orderID: Long): List<TasksTable>

    @Query("SELECT * FROM TasksTable")
    fun getAllData(): List<TasksTable>

    @Query("SELECT COUNT(id) FROM TasksTable")
    fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTask(data: TasksTable)

    @Query("DELETE FROM TasksTable WHERE Id > 0")
    fun deleteAllData()
}
