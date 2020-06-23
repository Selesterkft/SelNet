package hu.selester.seltransport.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import hu.selester.seltransport.database.tables.LogisticStatusesTable

@Dao
interface LogisticStatusesDao {
    @Query("SELECT * FROM LogisticStatusesTable WHERE transportId=:transportId")
    fun getByTransportId(transportId: Long): List<LogisticStatusesTable>

    @Query("SELECT * FROM LogisticStatusesTable WHERE cpDbId =:cpDbId")
    fun getByCpId(cpDbId: Long): LogisticStatusesTable

    @Query("SELECT * FROM LogisticStatusesTable WHERE transportId=:transportId AND addressType=:addressType AND :currentId<Id ORDER BY Id ASC")
    fun getNextStatuses(
        transportId: Long,
        addressType: Int,
        currentId: Long
    ): List<LogisticStatusesTable>

    @Insert
    fun insertStatus(logisticStatusesTable: LogisticStatusesTable): Long

    @Query("DELETE FROM LogisticStatusesTable")
    fun deleteAllData()
}