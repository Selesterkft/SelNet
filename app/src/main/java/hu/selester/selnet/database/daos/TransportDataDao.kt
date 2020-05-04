package hu.selester.selnet.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import hu.selester.selnet.database.tables.TransportDatasTable

@Dao
interface TransportDataDao {
    @Query("SELECT * FROM TransportDatasTable WHERE loginCode = :loginCode ORDER BY seqNum")
    fun getAll(loginCode : String) : List<TransportDatasTable>

    @Query("DELETE FROM TransportDatasTable")
    fun deleteAll()

    @Query("SELECT COUNT(id) FROM TransportDatasTable")
    fun getAllRowsNum() : Int

    @Insert
    fun insert(transportData : TransportDatasTable)
}
