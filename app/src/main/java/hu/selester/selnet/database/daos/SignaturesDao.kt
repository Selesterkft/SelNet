package hu.selester.selnet.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import hu.selester.selnet.database.tables.SignaturesTable

@Dao
interface SignaturesDao {
    @Insert
    fun insert(signaturesTable: SignaturesTable)

    @Query("SELECT COUNT(id) FROM SignaturesTable")
    fun getCount() : Int
}