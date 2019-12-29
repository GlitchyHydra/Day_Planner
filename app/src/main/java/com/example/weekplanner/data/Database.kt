package com.example.weekplanner.data

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@Entity(tableName = "plan")
data class Plan(
    @ColumnInfo(name = "Title") val title: String?,
    @ColumnInfo(name = "Note") val note: String?,
    @ColumnInfo(name = "Date") val date: String?,
    @ColumnInfo(name = "Location") val location: String?,
    @ColumnInfo(name = "Priority") val priority: Int?
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}


@Database(entities = [Plan::class], version = 1)
abstract class PlannerDatabase : RoomDatabase() {

    abstract fun planDAO(): PlanDao

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
                            Plan("", "", "", "", 3))
                }
            }
        }
    }

    private suspend fun insert(planDao: PlanDao?, plan: Plan) {
        val job = CoroutineScope(Dispatchers.IO).launch {
            if (isActive)
            planDao?.insertPlan(plan)
        }
    }
}

