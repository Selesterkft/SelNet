package hu.selester.seltransport.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import hu.selester.seltransport.database.tables.PicturesTable

/* uploaded field
    0 - not uploaded
    1 - try uploaded
    2 - uploaded
    3 - error upload 5 times
*/

@Dao
interface PicturesDao {
    @Query("SELECT * FROM PicturesTable WHERE id = :id")
    fun getById(id: Long): PicturesTable

    @Query("SELECT * FROM PicturesTable WHERE addressId = :addressId")
    fun getPhotosForAddress(addressId: Long): MutableList<PicturesTable>

    @Query("SELECT * FROM PicturesTable WHERE addressId = :addressId AND filePath = :filePath")
    fun getPhotosByAddressAndPath(addressId: Long, filePath: String): List<PicturesTable>

    @Query("SELECT * FROM PicturesTable")
    fun getAllData(): MutableList<PicturesTable>

    @Query("DELETE FROM PicturesTable")
    fun deleteAllData()

    @Query("DELETE FROM PicturesTable WHERE id=:id")
    fun deleteById(id: Long)

    @Insert
    fun insertPhoto(picturesTable: PicturesTable): Long
}
