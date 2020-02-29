package com.example.weekplanner.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
abstract class PlanDao {

    @Query("SELECT * FROM `plan`")
    abstract fun getAll(): LiveData<List<Plan>>

    @Query("DELETE FROM `plan`")
    abstract fun deleteAll()

    @Delete
    abstract fun deleteFailed(plans: List<Plan>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertStat(statistic: Statistic)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertStats(statistics: List<Statistic>)

    @Transaction
    open fun deleteFailedAndInsertInStat(plans: List<Plan>) {
        deleteFailed(plans)
        insertStats(plans.map { Statistic(it, false) }.toList())
    }

    @Transaction
    open fun deletePlanAndInsertStat(plan: Plan, isSolved: Boolean) {
        deletePlan(plan)
        insertStat(Statistic(plan, isSolved))
    }

    //для подгрузки из базы данных, когда выбираю конкретный список
    /*@Query(
        "SELECT * FROM plan " +
                "INNER JOIN loan ON plans.list_id = list.id " +
                "WHERE list.name LIKE :listName"
    )
    fun findAllByListName(listName: String): List<Plan>*/

    /*@Query("SELECT * FROM plan" +
            "WHERE listName LIKE :listName")
    fun findByListName(listName: String): List<Plan>*/

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insertPlan(plan: Plan)

    @Delete
    abstract fun deletePlan(plan: Plan)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    abstract fun updatePlan(plan: Plan)
}