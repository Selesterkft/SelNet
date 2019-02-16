package hu.selester.selnet.Database.Tables

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
class SystemTable(
    @field:PrimaryKey
    @field:ColumnInfo(name = "_key")
    var key: String, @field:ColumnInfo(name = "_value")
    var value: String?
) {

    override fun toString(): String {
        return "SystemTable{" +
                "key='" + key + '\''.toString() +
                ", value='" + value + '\''.toString() +
                '}'.toString()
    }
}