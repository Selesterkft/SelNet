package hu.selester.seltransport.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import hu.selester.seltransport.database.tables.UsersTable

@Dao
interface UsersDao {
    @Query("SELECT * FROM UsersTable")
    fun getAllData(): List<UsersTable>

    @Query("SELECT * FROM UsersTable WHERE masterKey IS NOT NULL AND registrationKey IS NOT NULL")
    fun getValidUsers(): List<UsersTable>

    @Query("DELETE FROM UsersTable")
    fun deleteAllData()

    @Query("SELECT * FROM UsersTable WHERE :telNumber = telephoneNumber")
    fun getUserByNumber(telNumber: String): List<UsersTable>

    @Query("SELECT * FROM UsersTable WHERE :telNumber = telephoneNumber AND masterKey IS NOT NULL AND registrationKey IS NOT NULL")
    fun getValidUserByNumber(telNumber: String): List<UsersTable>

    @Query("SELECT * FROM UsersTable WHERE :id = id")
    fun getUserById(id: Long): List<UsersTable>

    @Query("SELECT * FROM UsersTable WHERE :id = id AND masterKey IS NOT NULL AND registrationKey IS NOT NULL")
    fun getValidUserById(id: Long): List<UsersTable>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(usersTable: UsersTable): Long
}