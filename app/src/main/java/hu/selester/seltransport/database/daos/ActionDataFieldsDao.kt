package hu.selester.seltransport.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import hu.selester.seltransport.database.tables.ActionDataFieldsTable

@Dao
interface ActionDataFieldsDao {
    @Query("DELETE FROM ActionDataFieldsTable")
    fun deleteAllData()

    @Query("SELECT * FROM ActionDataFieldsTable WHERE actionId =:actionId")
    fun getByActionCpDbId(actionId: Long): List<ActionDataFieldsTable>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAddress(addressDataField: ActionDataFieldsTable): Long
}