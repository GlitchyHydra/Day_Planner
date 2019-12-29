package com.example.weekplanner

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.weekplanner.data.Plan
import com.example.weekplanner.data.PlanDao
import com.example.weekplanner.data.PlannerDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlannerRepository(application: Application) {
    private val planDao: PlanDao
    private val allPlans: LiveData<List<Plan>>

    init {
        val db = PlannerDatabase.getInstance(application.applicationContext)!!
        planDao = db.planDAO()
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
}