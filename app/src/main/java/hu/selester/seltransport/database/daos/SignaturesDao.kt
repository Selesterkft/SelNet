package hu.selester.seltransport.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import hu.selester.seltransport.database.tables.SignaturesTable

@Dao
interface SignaturesDao {
    @Query("SELECT COUNT(id) FROM SignaturesTable")
    fun getCount(): Int

    @Insert
    fun insert(signaturesTable: SignaturesTable): Long
}
