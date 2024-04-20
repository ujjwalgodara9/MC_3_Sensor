package com.example.mc_3.repository

import androidx.lifecycle.LiveData
import com.example.mc_3.Database.OrientationDao
import com.example.mc_3.Database.OrientationData

class OrientationRepository(private val orientationDao: OrientationDao) {

    val allOrientations: LiveData<List<OrientationData>> = orientationDao.getAllOrientationData()

    suspend fun insert(orientationData: OrientationData) {
        orientationDao.insertOrientationData(orientationData)
    }
}
