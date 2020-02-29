package com.example.weekplanner.data

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.*

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long): Date? {
        return Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date): Long {
        return date.getTime()
    }
}

/**
 *  Entity for storing a active tasks.
 *  Need for represent through adapter
 **/
@Entity(tableName = "plan")
data class Plan(
    @ColumnInfo(name = "Title") val title: String?,
    @ColumnInfo(name = "Note") val note: String?,
    @ColumnInfo(name = "Date") val date: Long?,
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
    @ColumnInfo(name = "Date") val date: Long?,
    @ColumnInfo(name = "Location") val location: String?,
    @ColumnInfo(name = "Priority") val priority: Int?,
    @ColumnInfo(name = "Category") val category: String?
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "Stat_id")
    var id: Int = 0

    constructor(plan: Plan, isSolved: Boolean) : this(
        isSolved, plan.date,
        plan.location, plan.priority, plan.category
    )

    override fun equals(other: Any?): Boolean {
        val stat = other as Statistic
        val calendar = Calendar.getInstance()
        calendar.time = Date(stat.date!!)
        val otherCalendar = calendar
        calendar.time = Date(this.date!!)

        return otherCalendar == calendar && stat.isSolved == this.isSolved
    }
}

@Database(entities = [Plan::class, Statistic::class], version = 1)
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
                        instance!!.insert(
                            instance?.planDAO(),
                            Plan("Kek", "Kek", Calendar.getInstance().timeInMillis,
                                "New Orlean", 3, "Category")
                        )
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

