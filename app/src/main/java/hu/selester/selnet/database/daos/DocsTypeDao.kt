package hu.selester.selnet.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import hu.selester.selnet.database.tables.DocsTypeTable

@Dao
interface DocsTypeDao {

    @Query("SELECT * FROM DocsTypeTable ORDER BY docName")
    fun getAll() : List<DocsTypeTable>

    @Query("SELECT transactId FROM DocsTypeTable ORDER BY transactId DESC LIMIT 1")
    fun getLastTransactID(): Int

    @Query("DELETE FROM DocsTypeTable")
    fun deleteAll()

    @Query("SELECT COUNT(id) FROM DocsTypeTable")
    fun getAllRowsNum() : Int

    @Insert
    fun insert(docsTypeTable : DocsTypeTable)
}
