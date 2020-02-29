package com.example.weekplanner.views

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weekplanner.repositories.PlannerRepository
import com.example.weekplanner.data.Plan

class PlanViewModel(application: Application): AndroidViewModel(application) {

    private val plannerRepository: PlannerRepository
    private val allPlans: LiveData<List<Plan>>

    init {
        plannerRepository =
            PlannerRepository(application)
        allPlans = plannerRepository.getAllPlans()
    }

    fun getAllPlans(): LiveData<List<Plan>> {
        return allPlans
    }

    fun insert(plan: Plan) {
        plannerRepository.insertPlan(plan)
    }

    fun delete(plan: Plan, isSolved: Boolean) {
        plannerRepository.deletePlan(plan, isSolved)
    }

    fun update(plan: Plan) {
        plannerRepository.updatePlan(plan)
    }

    fun deleteFailed(plans: List<Plan>) {
        plannerRepository.deleteFailed(plans)
    }

    fun deleteAll(){
        plannerRepository.deleteAll()
    }

    /*fun getAllStats(): List<Statistic> {
        return plannerRepository.getAllStat()
    }*/
}

class PlanViewModelFactory(val application: Application) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(Application::class.java).newInstance(application)
    }
}