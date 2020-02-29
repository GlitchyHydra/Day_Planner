package com.example.weekplanner.repositories

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.weekplanner.data.PlannerDatabase
import com.example.weekplanner.data.StatDao
import com.example.weekplanner.data.Statistic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StatRepository(application: Application) {

    private val statDao: StatDao
    private val allStats: LiveData<List<Statistic>>

    init {
        val db = PlannerDatabase.getInstance(
            application.applicationContext
        )!!
        statDao = db.statDAO()
        allStats = statDao.getAllStat()
    }

    fun deleteAllStat() {
        CoroutineScope(Dispatchers.IO).launch {
            statDao.deleteAllStat()
        }
    }

    fun getAllStat() : LiveData<List<Statistic>> {
        return allStats
    }
}