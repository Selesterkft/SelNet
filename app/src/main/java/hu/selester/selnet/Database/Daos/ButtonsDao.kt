package hu.selester.selnet.Database.Daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import hu.selester.selnet.Database.Tables.ButtonsTable

@Dao
interface ButtonsDao{

    @Query("Select * from ButtonsTable")
    fun getAllData(): List<ButtonsTable>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addButtonData(buttonsTable: ButtonsTable)

}