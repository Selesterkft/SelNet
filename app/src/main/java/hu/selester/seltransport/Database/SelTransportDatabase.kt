package hu.selester.seltransport.Database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import hu.selester.seltransport.Database.Daos.DocsTypeDao
import hu.selester.seltransport.Database.Daos.PhotosDao
import hu.selester.seltransport.Database.Daos.SystemDao
import hu.selester.seltransport.Database.Daos.TransportDatasDao
import hu.selester.seltransport.Database.Tables.DocsTypeTable
import hu.selester.seltransport.Database.Tables.PhotosTable
import hu.selester.seltransport.Database.Tables.SystemTable
import hu.selester.seltransport.Database.Tables.TransportDatasTable

@Database(entities = [TransportDatasTable::class, PhotosTable::class, DocsTypeTable::class, SystemTable::class], version = 12)
abstract class SelTransportDatabase: RoomDatabase() {

    abstract fun transportDatasDao ()   : TransportDatasDao
    abstract fun docsTypeDao ()         : DocsTypeDao
    abstract fun photosDao()            : PhotosDao
    abstract fun systemDao()            : SystemDao


    companion object {
        private var INSTANCE: SelTransportDatabase? = null
        fun getInstance(context: Context): SelTransportDatabase? {
            if (INSTANCE == null) {
                synchronized(SelTransportDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        SelTransportDatabase::class.java, "seltransport.db")
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