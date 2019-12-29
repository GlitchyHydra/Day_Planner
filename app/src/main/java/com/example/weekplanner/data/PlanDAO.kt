package com.example.weekplanner.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PlanDao {
    @Query("SELECT * FROM `plan`")
    fun getAll(): LiveData<List<Plan>>

    @Query("DELETE FROM `plan`")
    fun deleteAll()

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
    fun insertPlan(plan: Plan)

    @Delete
    fun deletePlan(plan: Plan)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updatePlan(plan: Plan)
}