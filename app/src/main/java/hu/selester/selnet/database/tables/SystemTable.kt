package hu.selester.selnet.database.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

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