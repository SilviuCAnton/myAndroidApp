package com.silviucanton.myandroidapp.bugs.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bugs")
data class Bug(
    @PrimaryKey @ColumnInfo(name = "id") val id: Long,
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "description") var description: String,
    @ColumnInfo(name = "dateReported") var dateReported: String,
    @ColumnInfo(name = "severity") var severity: Int,
    @ColumnInfo(name = "solved") var solved: Boolean,
    @ColumnInfo(name = "userId") var userId: String
) {
    override fun toString(): String = "$title $description $dateReported $solved"
}