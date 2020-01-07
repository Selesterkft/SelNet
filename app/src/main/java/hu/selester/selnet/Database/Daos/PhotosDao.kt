package hu.selester.selnet.Database.Daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import hu.selester.selnet.Database.Tables.PhotosTable

/* uploaded field
    0 - not uploaded
    1 - try uploaded
    2 - uploaded
    3 - error upload 5 times
*/

@Dao
interface PhotosDao {

    @Query("Select * from PhotosTable where id = :id")
    fun getDataWithID(id : Long) : PhotosTable

    @Query("Select * from PhotosTable where orderId = :orderId")
    fun getData(orderId : Int) : List<PhotosTable>

    @Query("Select * from PhotosTable where orderId = :orderId and addrId=:addrId and uploaded = 1")
    fun getBeUploadData(orderId : Int, addrId: Int) : List<PhotosTable>

    @Query("Select * from PhotosTable where orderId = :orderId and addrId=:addrId")
    fun getPositionData(orderId : Int, addrId: Int) : List<PhotosTable>

    @Query("Select * from PhotosTable where uploaded = 0 and tried < 6")
    fun getAllNotUploadedData() : List<PhotosTable>

    @Query("Select * from PhotosTable where uploaded = 1 and tried < 6")
    fun getAllTryUploadedData() : List<PhotosTable>

    @Query("Select * from PhotosTable where uploaded = 2 and tried < 6")
    fun getAllUploadedData() : List<PhotosTable>

    @Query("Select * from PhotosTable where uploaded = 3 and tried > 5")
    fun getAllErrorUploadedData() : List<PhotosTable>

    @Insert
    fun insert(photosTable : PhotosTable)

    @Query("Delete from PhotosTable")
    fun deleteAll()

    @Query("Select count(id) from PhotosTable")
    fun getAllRowsNum() : Integer

    @Query("Update PhotosTable set uploaded = :status where id= :id")
    fun setUploadStatus(id:Long, status:Int)

    @Query("Update PhotosTable set uploaded = 0, tried = tried + 1 where id = :id")
    fun addTried(id : Long)


}