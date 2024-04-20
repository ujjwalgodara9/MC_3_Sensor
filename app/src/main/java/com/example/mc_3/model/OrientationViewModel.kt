package com.example.mc_3.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.mc_3.Database.OrientationData
import com.example.mc_3.Database.OrientationDatabase
import com.example.mc_3.repository.OrientationRepository
import kotlinx.coroutines.launch

class OrientationViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: OrientationRepository
    val allOrientations: LiveData<List<OrientationData>>

    init {
        val orientationDao = OrientationDatabase.getDatabase(application).orientationDao()
        repository = OrientationRepository(orientationDao)
        allOrientations = repository.allOrientations
    }

    fun insert(orientationData: OrientationData) = viewModelScope.launch {
        repository.insert(orientationData)
    }
}

