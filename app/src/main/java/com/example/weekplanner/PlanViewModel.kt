package com.example.weekplanner

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weekplanner.data.Plan

class PlanViewModel(application: Application): AndroidViewModel(application) {

    private val plannerRepository: PlannerRepository
    private val allPlans: LiveData<List<Plan>>

    init {
        plannerRepository = PlannerRepository(application)
        allPlans = plannerRepository.getAllPlans()
    }

    fun getAllPlanes(): LiveData<List<Plan>> {
        return allPlans
    }

    fun insert(plan: Plan) {
        plannerRepository.insertPlan(plan)
    }

    fun delete(plan: Plan) {
        plannerRepository.deletePlan(plan)
    }

    fun update(plan: Plan) {
        plannerRepository.updatePlan(plan)
    }

    fun deleteAll(){
        plannerRepository.deleteAll()
    }
}

class PlanViewModelFactory(val application: Application) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(Application::class.java).newInstance(application)
    }
}