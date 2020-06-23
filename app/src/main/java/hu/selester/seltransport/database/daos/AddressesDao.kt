package hu.selester.seltransport.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import hu.selester.seltransport.database.tables.AddressesTable

@Dao
interface AddressesDao {
    @Query("DELETE FROM AddressesTable WHERE id > 0")
    fun deleteAllData()

    @Query("SELECT * FROM AddressesTable")
    fun getAllData(): List<AddressesTable>

    @Query("SELECT * FROM AddressesTable WHERE transportId =:transportId")
    fun getAddressesForTransport(transportId: Long): List<AddressesTable>

    @Query("SELECT * FROM AddressesTable WHERE id =:id")
    fun getById(id: Long): AddressesTable

    @Query("SELECT * FROM AddressesTable WHERE cpDbId =:cpDbId")
    fun getByCpDbId(cpDbId: Long): AddressesTable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAddress(address: AddressesTable): Long
}