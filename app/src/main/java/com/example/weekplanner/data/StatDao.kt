package com.example.weekplanner.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface StatDao {
    /**
     * Stat DAO
     */

    @Query("DELETE FROM 'statistic'")
    fun deleteAllStat()

    @Query("SELECT * FROM 'statistic'")
    fun getAllStat(): LiveData<List<Statistic>>
}