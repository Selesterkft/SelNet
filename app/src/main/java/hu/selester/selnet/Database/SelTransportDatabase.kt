package hu.selester.selnet.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import hu.selester.selnet.Database.Daos.*
import hu.selester.selnet.Database.Tables.*

@Database(entities = [TransportDatasTable::class, PhotosTable::class, DocsTypeTable::class, SystemTable::class, ButtonsTable::class, TasksTable::class, CompaniesTables::class], version = 17)
abstract class SelTransportDatabase: RoomDatabase() {

    abstract fun transportDatasDao ()   : TransportDatasDao
    abstract fun docsTypeDao ()         : DocsTypeDao
    abstract fun photosDao()            : PhotosDao
    abstract fun systemDao()            : SystemDao
    abstract fun buttonsDao()           : ButtonsDao
    abstract fun tasksDAO()             : TasksDAO
    abstract fun companiesDao()         : CompaniesDao


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