package hu.selester.selnet.Database.Daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import hu.selester.selnet.Database.Tables.DocsTypeTable

@Dao
interface DocsTypeDao {

    @Query("Select * from DocsTypeTable order by docName")
    fun getAll() : List<DocsTypeTable>

    @Query("Select transactId from DocsTypeTable order by transactId desc limit 1")
    fun getLastTransactID(): Int

    @Insert
    fun insert(docsTypeTable : DocsTypeTable)

    @Query("Delete from DocsTypeTable")
    fun deleteAll()

    @Query("Select count(id) from DocsTypeTable")
    fun getAllRowsNum() : Integer

}