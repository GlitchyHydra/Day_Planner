package com.example.weekplanner.data

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.weekplanner.data.*
import kotlinx.coroutines.*

class PlannerRepository(application: Application) {
    private val planDao: PlanDao
    private val statDao: StatDao
    private val allPlans: LiveData<List<Plan>>

    init {
        val db = PlannerDatabase.getInstance(application.applicationContext)!!
        planDao = db.planDAO()
        statDao = db.statDAO()
        allPlans = planDao.getAll()
    }

    fun getAllPlans(): LiveData<List<Plan>> {
        return allPlans
    }

    fun updatePlan(plan: Plan) {
        CoroutineScope(Dispatchers.IO).launch {
            planDao.updatePlan(plan)
        }
    }

    fun deletePlan(plan: Plan) {
        CoroutineScope(Dispatchers.IO).launch {
            planDao.deletePlan(plan)
        }
    }

    fun insertPlan(plan: Plan) {
        CoroutineScope(Dispatchers.IO).launch {
            planDao.insertPlan(plan)
        }
    }

    fun deleteAll() {
        CoroutineScope(Dispatchers.IO).launch {
            planDao.deleteAll()
        }
    }

    fun insertStat(plan: Plan, isSolved : Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            val newStat = Statistic(isSolved,
                plan.date,plan.location,
                plan.priority,plan.category)
            statDao.insertStat(newStat)
        }
    }

    fun deleteAllStat() {
        CoroutineScope(Dispatchers.IO).launch {
            statDao.deleteAllStat()
        }
    }

    fun getAllStat() : List<Statistic> {
        val allStats = mutableListOf<Statistic>()
        val job = CoroutineScope(Dispatchers.IO).launch{
            allStats.addAll(statDao.getAllStat())
        }
        CoroutineScope(Dispatchers.Main).launch {
            job.join()
        }
        return allStats
    }
}