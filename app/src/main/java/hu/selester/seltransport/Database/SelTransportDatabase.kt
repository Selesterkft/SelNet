package hu.selester.seltransport.Database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import hu.selester.seltransport.Database.Daos.TransportDatasDao
import hu.selester.seltransport.Database.Tables.TransportDatasTable

@Database(entities = [TransportDatasTable::class], version = 5)
abstract class SelTransportDatabase: RoomDatabase() {

    abstract fun transportDatasDao (): TransportDatasDao

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