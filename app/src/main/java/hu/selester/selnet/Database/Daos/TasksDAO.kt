package hu.selester.selnet.Database.Daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import hu.selester.selnet.Database.Tables.TasksTable

@Dao
interface TasksDAO{

    @Query("Select * from TasksTable where orderId = :orderID and addressId = :addressId")
    fun getAddressData(orderID: Long, addressId: Long): TasksTable

    @Query("Select * from TasksTable where orderId = :orderID")
    fun getOrderData(orderID: Long): List<TasksTable>

    @Query("Select * from TasksTable")
    fun getAllData(): List<TasksTable>

    @Query("Select count(id) from TasksTable")
    fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTask(data: TasksTable)

    @Query("Delete from TasksTable where Id > 0")
    fun deleteAllData()
}