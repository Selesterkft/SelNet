package hu.selester.seltransport.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import hu.selester.seltransport.database.daos.*
import hu.selester.seltransport.database.tables.*

@Database(
    entities = [AddressesTable::class, GoodsTable::class, LogisticStatusesTable::class, PhotosTable::class, SignaturesTable::class,
        SyncTasksTable::class, TaskActionsTable::class, TaskStatusTable::class, TransportsTable::class, UsersTable::class],
    version = 1,
    exportSchema = false
)
abstract class SelTransportDatabase : RoomDatabase() {
    abstract fun addressesDao(): AddressesDao
    abstract fun goodsDao(): GoodsDao
    abstract fun photosDao(): PhotosDao
    abstract fun logisticStatusesDao(): LogisticStatusesDao
    abstract fun signaturesDao(): SignaturesDao
    abstract fun syncTasksDao(): SyncTasksDao
    abstract fun taskActionsDao(): TaskActionsDao
    abstract fun taskStatusDao(): TaskStatusDao
    abstract fun transportsDao(): TransportsDao
    abstract fun usersDao(): UsersDao


    companion object {
        private var INSTANCE: SelTransportDatabase? = null
        fun getInstance(context: Context): SelTransportDatabase? {
            if (INSTANCE == null) {
                synchronized(SelTransportDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        SelTransportDatabase::class.java, "seltransport.db"
                    )
                        .allowMainThreadQueries()
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}