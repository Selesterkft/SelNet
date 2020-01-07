package hu.selester.selnet.Database.Daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import hu.selester.selnet.Database.Tables.CompaniesTables

@Dao
interface CompaniesDao{

    @Query("Select * from CompaniesTables ")
    fun getAllData():List<CompaniesTables>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCompanies(data: CompaniesTables)

    @Query("Select count(id) from CompaniesTables")
    fun getCount():Int

}