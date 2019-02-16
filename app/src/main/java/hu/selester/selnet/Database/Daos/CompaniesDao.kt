package hu.selester.selnet.Database.Daos

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
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