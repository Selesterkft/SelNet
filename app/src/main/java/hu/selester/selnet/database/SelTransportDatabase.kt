package hu.selester.selnet.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import hu.selester.selnet.database.daos.*
import hu.selester.selnet.database.tables.*

@Database(
    entities = [TransportDatasTable::class, PhotosTable::class, DocsTypeTable::class, SystemTable::class, ButtonsTable::class, TasksTable::class, CompaniesTable::class, SignaturesTable::class],
    version = 18,
    exportSchema = false
)
abstract class SelTransportDatabase : RoomDatabase() {

    abstract fun transportDataDao(): TransportDataDao
    abstract fun docsTypeDao(): DocsTypeDao
    abstract fun photosDao(): PhotosDao
    abstract fun systemDao(): SystemDao
    abstract fun buttonsDao(): ButtonsDao
    abstract fun tasksDao(): TasksDao
    abstract fun companiesDao(): CompaniesDao
    abstract fun signaturesDao(): SignaturesDao


    companion object {
        private var INSTANCE: SelTransportDatabase? = null
        fun getInstance(context: Context): SelTransportDatabase? {
            if (INSTANCE == null) {
                synchronized(SelTransportDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.getApplicationContext(),
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