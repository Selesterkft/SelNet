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
    fun getByAddressId(addressId: Long): List<GoodsTable>

    @Insert
    fun insertGoods(goodsTable: GoodsTable): Long
}