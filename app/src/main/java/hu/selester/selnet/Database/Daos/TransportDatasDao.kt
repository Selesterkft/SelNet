package hu.selester.selnet.Database.Daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import hu.selester.selnet.Database.Tables.TransportDatasTable

@Dao
interface TransportDatasDao {

    @Query("Select * from TransportDatasTable where loginCode = :loginCode order by seqNum")
    fun getAll(loginCode : String) : List<TransportDatasTable>

    @Insert
    fun insert(transportData : TransportDatasTable)

    @Query("Delete from TransportDatasTable")
    fun deleteAll()

    @Query("Select count(id) from TransportDatasTable")
    fun getAllRowsNum() : Integer


}