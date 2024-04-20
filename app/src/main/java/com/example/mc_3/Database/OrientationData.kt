package com.example.mc_3.Database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orientation_data")
data class OrientationData(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "time_stamp") val timeStamp: Long,
    @ColumnInfo(name = "x_angle") val xAngle: Float,
    @ColumnInfo(name = "y_angle") val yAngle: Float,
    @ColumnInfo(name = "z_angle") val zAngle: Float
)
