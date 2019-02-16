package hu.selester.selnet.Database.Daos

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import hu.selester.selnet.Database.Tables.ButtonsTable

@Dao
interface ButtonsDao{

    @Query("Select * from ButtonsTable")
    fun getAllData(): List<ButtonsTable>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addButtonData(buttonsTable: ButtonsTable)

}