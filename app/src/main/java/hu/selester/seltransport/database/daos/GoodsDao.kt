package hu.selester.seltransport.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import hu.selester.seltransport.database.tables.GoodsTable

@Dao
interface GoodsDao {
    @Query("DELETE FROM GoodsTable")
    fun deleteAllData()

    @Query("SELECT * FROM GoodsTable WHERE addressId =:addressId")
    fun getByAddressCpDbId(addressId: Long): List<GoodsTable>

    @Query("SELECT SUM(weight) FROM GoodsTable WHERE addressId =:addressId")
    fun getSumWeightByAddressCpDbId(addressId: Long): Int

    @Query("SELECT SUM(volume) FROM GoodsTable WHERE addressId =:addressId")
    fun getSumVolumeByAddressCpDbId(addressId: Long): Double

    @Query("SELECT SUM(space) FROM GoodsTable WHERE addressId =:addressId")
    fun getSumSpaceByAddressCpDbId(addressId: Long): Double

    @Insert
    fun insertGoods(goodsTable: GoodsTable): Long
}