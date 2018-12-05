package hu.selester.seltransport.Database.Daos

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import hu.selester.seltransport.Database.Tables.PhotosTable
import hu.selester.seltransport.Database.Tables.TransportDatasTable

@Dao
interface PhotosDao {

    @Query("Select * from PhotosTable where orderId = :orderId")
    fun getData(orderId : Int) : List<PhotosTable>

    @Query("Select * from PhotosTable where orderId = :orderId and addrId=:addrId")
    fun getPositionData(orderId : Int, addrId: Int) : List<PhotosTable>

    @Insert
    fun insert(photosTable : PhotosTable)

    @Query("Delete from PhotosTable")
    fun deleteAll()

    @Query("Select count(id) from PhotosTable")
    fun getAllRowsNum() : Integer

}