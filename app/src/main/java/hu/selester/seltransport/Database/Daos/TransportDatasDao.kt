package hu.selester.seltransport.Database.Daos

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import hu.selester.seltransport.Database.Tables.TransportDatasTable

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