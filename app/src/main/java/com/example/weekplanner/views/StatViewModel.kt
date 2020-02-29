package com.example.weekplanner.views

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weekplanner.data.Statistic
import com.example.weekplanner.repositories.StatRepository

class StatViewModel(application: Application): AndroidViewModel(application) {

    private val statRepository: StatRepository
    private val allStats: LiveData<List<Statistic>>

    init {
        statRepository = StatRepository(application)
        allStats = statRepository.getAllStat()
    }

    fun getAllStats(): LiveData<List<Statistic>> {
        return allStats
    }

}

class StatViewModelFactory(val application: Application) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(Application::class.java).newInstance(application)
    }
}