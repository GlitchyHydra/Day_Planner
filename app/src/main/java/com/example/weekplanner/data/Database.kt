package com.example.weekplanner.data

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 *  Entity for storing a active tasks.
 *  Need for represent through adapter
 **/
@Entity(tableName = "plan")
data class Plan(
    @ColumnInfo(name = "Title") val title: String?,
    @ColumnInfo(name = "Note") val note: String?,
    @ColumnInfo(name = "Date") val date: String?,
    @ColumnInfo(name = "Location") val location: String?,
    @ColumnInfo(name = "Priority") val priority: Int?,
    @ColumnInfo(name = "Category") val category: String?
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}

/**
 *  Entity for storing a finish tasks.
 *  Need for make infographic
 **/
@Entity(tableName = "statistic")
data class Statistic(
    @ColumnInfo(name = "isSolved") val isSolved: Boolean,
    @ColumnInfo(name = "Date") val date: String?,
    @ColumnInfo(name = "Location") val location: String?,
    @ColumnInfo(name = "Priority") val priority: Int?,
    @ColumnInfo(name = "Category") val category: String?
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "Stat_id") var id: Int = 0
}

@Database(entities = [Plan::class, Statistic::class], version = 4)
abstract class PlannerDatabase : RoomDatabase() {

    abstract fun planDAO(): PlanDao
    abstract fun statDAO(): StatDao

    companion object {
        private var instance: PlannerDatabase? = null

        fun getInstance(context: Context): PlannerDatabase? {
            if (instance == null) {
                synchronized(PlannerDatabase::class) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        PlannerDatabase::class.java, "week-planner-db"
                    )
                        .fallbackToDestructiveMigration() // when version increments, it migrates (deletes db and creates new) - else it crashes
                        .addCallback(roomCallback)
                        .build()
                }
            }
            return instance
        }

        fun destroyInstance() {
            instance = null
        }

        private val roomCallback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                CoroutineScope(Dispatchers.IO).launch {
                    if (isActive)
                        instance!!.insert(instance?.planDAO(),
                            Plan("Kek", "Kek", "", "New Orlean", 3, "Category"))
                }
            }
        }
    }

    private fun insert(planDao: PlanDao?, plan: Plan) {
        val job = CoroutineScope(Dispatchers.IO).launch {
            if (isActive)
            planDao?.insertPlan(plan)
        }
    }
}

