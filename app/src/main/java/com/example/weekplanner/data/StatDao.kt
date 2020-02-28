package com.example.weekplanner.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface StatDao {
    /**
     * Stat DAO
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertStat(statistic: Statistic)

    @Query("DELETE FROM 'statistic'")
    fun deleteAllStat()

    @Query("SELECT * FROM 'statistic'")
    fun getAllStat(): List<Statistic>
}