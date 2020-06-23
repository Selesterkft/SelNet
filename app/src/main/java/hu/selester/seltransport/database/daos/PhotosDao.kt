package hu.selester.seltransport.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import hu.selester.seltransport.database.tables.PhotosTable

/* uploaded field
    0 - not uploaded
    1 - try uploaded
    2 - uploaded
    3 - error upload 5 times
*/

@Dao
interface PhotosDao {
    @Query("SELECT * FROM PhotosTable WHERE id = :id")
    fun getById(id: Long): PhotosTable

    @Query("SELECT * FROM PhotosTable WHERE addressId = :addressId")
    fun getPhotosForAddress(addressId: Long): MutableList<PhotosTable>

    @Query("SELECT * FROM PhotosTable")
    fun getAllData(): MutableList<PhotosTable>

    @Query("DELETE FROM PhotosTable")
    fun deleteAllData()

    @Query("DELETE FROM photostable WHERE id=:id")
    fun deleteById(id: Long)

    @Insert
    fun insertPhoto(photosTable: PhotosTable): Long
}
