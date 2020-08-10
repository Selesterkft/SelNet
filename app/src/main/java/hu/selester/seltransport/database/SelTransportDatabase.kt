package hu.selester.seltransport.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import hu.selester.seltransport.database.daos.*
import hu.selester.seltransport.database.tables.*

@Database(
    entities = [AddressesTable::class, ActionDataFieldsTable::class, GoodsTable::class, LogisticStatusesTable::class, PicturesTable::class, SignaturesTable::class,
        SyncTasksTable::class, TaskActionsTable::class, TransportsTable::class, UsersTable::class],
    version = 2,
    exportSchema = false
)
abstract class SelTransportDatabase : RoomDatabase() {
    abstract fun addressesDao(): AddressesDao
    abstract fun goodsDao(): GoodsDao
    abstract fun picturesDao(): PicturesDao
    abstract fun logisticStatusesDao(): LogisticStatusesDao
    abstract fun signaturesDao(): SignaturesDao
    abstract fun syncTasksDao(): SyncTasksDao
    abstract fun taskActionsDao(): TaskActionsDao
    abstract fun transportsDao(): TransportsDao
    abstract fun usersDao(): UsersDao
    abstract fun actionDataFieldsDao(): ActionDataFieldsDao


    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE TABLE `ActionDataFieldsTable` (`id`  INTEGER, `cpDbId`  INTEGER NOT NULL, " +
                            "`actionId` INTEGER NOT NULL, `hu` TEXT NOT NULL, `en` TEXT NOT NULL, `de` TEXT NOT NULL, `type` INTEGER NOT NULL," +
                            "`value` TEXT NOT NULL, PRIMARY KEY(`id`))"
                )
            }
        }
        private var INSTANCE: SelTransportDatabase? = null
        fun getInstance(context: Context): SelTransportDatabase {
            if (INSTANCE == null) {
                synchronized(SelTransportDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        SelTransportDatabase::class.java, "seltransport.db"
                    )
                        .addMigrations(MIGRATION_1_2)
                        .allowMainThreadQueries()
                        .build()
                }
            }
            return INSTANCE!!
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}