package hu.selester.selnet.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import hu.selester.selnet.database.tables.PhotosTable

/* uploaded field
    0 - not uploaded
    1 - try uploaded
    2 - uploaded
    3 - error upload 5 times
*/

@Dao
interface PhotosDao {
    @Query("SELECT * FROM PhotosTable WHERE id = :id")
    fun getDataWithID(id : Long) : PhotosTable

    @Query("SELECT * FROM PhotosTable WHERE orderId = :orderId")
    fun getData(orderId : Int) : List<PhotosTable>

    @Query("SELECT * FROM PhotosTable WHERE orderId = :orderId AND addrId=:addrId AND uploaded = 1")
    fun getBeUploadData(orderId : Int, addrId: Int) : List<PhotosTable>

    @Query("SELECT * FROM PhotosTable WHERE orderId = :orderId AND addrId=:addrId")
    fun getPositionData(orderId : Int, addrId: Int) : List<PhotosTable>

    @Query("SELECT * FROM PhotosTable WHERE uploaded = 0 AND tried < 6")
    fun  getAllNotUploadedData() : List<PhotosTable>

    @Query("SELECT * FROM PhotosTable WHERE uploaded = 1 AND tried < 6")
    fun getAllTryUploadedData() : List<PhotosTable>

    @Query("SELECT * FROM PhotosTable WHERE uploaded = 2 AND tried < 6")
    fun getAllUploadedData() : List<PhotosTable>

    @Query("SELECT * FROM PhotosTable WHERE uploaded = 3 AND tried > 5")
    fun getAllErrorUploadedData() : List<PhotosTable>

    @Query("DELETE FROM PhotosTable")
    fun deleteAll()

    @Query("SELECT COUNT(id) FROM PhotosTable")
    fun getAllRowsNum() : Int

    @Query("UPDATE PhotosTable SET uploaded = :status WHERE id= :id")
    fun setUploadStatus(id:Long, status:Int)

    @Query("UPDATE PhotosTable SET uploaded = 0, tried = tried + 1 WHERE id = :id")
    fun addTried(id : Long)

    @Insert
    fun insert(photosTable : PhotosTable)
}
