package hu.selester.seltransport.Database.Daos

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import hu.selester.seltransport.Database.Tables.SystemTable

@Dao
interface SystemDao {

    @Query("Select _value from SystemTable where _key=:key limit 1")
    fun getValue(key: String): String

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun setValue(systemTable: SystemTable)

}