package hu.selester.selnet.Database.Daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import hu.selester.selnet.Database.Tables.SystemTable

@Dao
interface SystemDao {

    @Query("Select _value from SystemTable where _key=:key limit 1")
    fun getValue(key: String): String

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun setValue(systemTable: SystemTable)

}