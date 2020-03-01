package com.example.weekplanner.views

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weekplanner.repositories.PlannerRepository
import com.example.weekplanner.data.Plan
import kotlinx.coroutines.*
import java.util.*

class PlanViewModel(application: Application) : AndroidViewModel(application) {

    private val plannerRepository: PlannerRepository
    private val allPlans: LiveData<List<Plan>>
    private val viewModelJob = SupervisorJob()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private val calendar get() = Calendar.getInstance()

    init {
        plannerRepository =
            PlannerRepository(application)
        allPlans = plannerRepository.getAllPlans()
        uiScope.launch { checkAndDeleteFailed() }
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

    fun deleteAll() {
        plannerRepository.deleteAll()
    }

    private fun compareCalendars(date: Long, calendarLimit: Calendar): Boolean {
        val calendarCurrent = Calendar.getInstance()
        calendarCurrent.time = Date(date)
        val daysEqualOrMore = calendarCurrent.get(Calendar.DAY_OF_YEAR) >=
                calendarLimit.get(Calendar.DAY_OF_YEAR)
        val yearEqual = calendarCurrent.get(Calendar.YEAR) ==
                calendarLimit.get(Calendar.YEAR)
        return !daysEqualOrMore || !yearEqual
    }

    private suspend fun checkAndDeleteFailed() {
        withContext(Dispatchers.IO) {
            while (isActive) {
                val plans = allPlans.value
                if (plans != null)
                    deleteFailed(plans.filter { compareCalendars(it.date!!, calendar) })
                delay(1000)
            }
        }
    }

    override fun onCleared() {
        uiScope.cancel()
        super.onCleared()
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