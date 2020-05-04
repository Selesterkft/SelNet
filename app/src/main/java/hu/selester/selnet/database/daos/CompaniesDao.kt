package hu.selester.selnet.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import hu.selester.selnet.database.tables.CompaniesTable

@Dao
interface CompaniesDao{
    @Query("SELECT * FROM CompaniesTable ")
    fun getAllData():List<CompaniesTable>

    @Query("SELECT COUNT(id) FROM CompaniesTable")
    fun getCount():Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCompanies(data: CompaniesTable)
}
