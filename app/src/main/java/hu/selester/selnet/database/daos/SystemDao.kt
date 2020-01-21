package hu.selester.selnet.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import hu.selester.selnet.database.tables.SystemTable

@Dao
interface SystemDao {

    @Query("SELECT _value FROM SystemTable WHERE _key=:key LIMIT 1")
    fun getValue(key: String): String

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun setValue(systemTable: SystemTable)

}
