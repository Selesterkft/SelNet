package hu.selester.selnet.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import hu.selester.selnet.database.tables.ButtonsTable

@Dao
interface ButtonsDao{
    @Query("SELECT * FROM ButtonsTable")
    fun getAllData(): List<ButtonsTable>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addButtonData(buttonsTable: ButtonsTable)
}
