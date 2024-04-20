package com.example.mc_3.Database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface OrientationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrientationData(orientationData: OrientationData)

    @Query("SELECT * FROM orientation_data")
    fun getAllOrientationData(): LiveData<List<OrientationData>>
}
