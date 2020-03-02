package com.example.weekplanner.activities

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weekplanner.R
import com.example.weekplanner.adapters.*
import com.example.weekplanner.broadcasters.ReminderBroadcaster
import com.example.weekplanner.data.Plan
import com.example.weekplanner.data.PlannerDatabase
import com.example.weekplanner.views.PlanViewModel
import com.example.weekplanner.views.PlanViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {

    companion object {
        const val ADD_NOTE_REQUEST = 101
        const val EDIT_NOTE_REQUEST = 102
        const val CHANNEL_ID = "dayNotifier"
        const val EXTRA_CHECK_DAY = "tomorrowCheck"
    }

    var planViewModel: PlanViewModel? = null

    private fun getDayOfYear(timeInMillis: Long): Int {
        val calendar = Calendar.getInstance()
        calendar.time = Date(timeInMillis)
        return calendar.get(Calendar.DAY_OF_MONTH)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createChannel()

        buttonAddNote.setOnClickListener {
            startActivityForResult(
                Intent(this, AddingActivity::class.java),
                ADD_NOTE_REQUEST
            )
        }

        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.setHasFixedSize(true)

        val adapter = PlansAdapter(
            ContextCompat.getColor(applicationContext, R.color.colorNormal),
            ContextCompat.getColor(applicationContext, R.color.colorImportant),
            ContextCompat.getColor(applicationContext, R.color.colorVeryImportant),
            this
        )

        planViewModel = ViewModelProvider(this, PlanViewModelFactory(application))
            .get(PlanViewModel::class.java)
        planViewModel!!.getAllPlans().observe(this, Observer<List<Plan>> {
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show()
            val newList = it.map { plan -> PlanItem(plan) }

            val calendarToday = Calendar.getInstance()

            val total = mutableListOf<ListItem>()

            val todayPlans = newList.filter { listItem ->
                getDayOfYear(listItem.plan.date!!) == calendarToday
                    .get(Calendar.DAY_OF_MONTH)
            }
            if (todayPlans.isNotEmpty()) {
                total.add(HeaderItem(getString(R.string.today)))
                total.addAll(todayPlans)
            }
            val tomorrowPlans = newList.filter { listItem ->
                getDayOfYear(listItem.plan.date!!) != calendarToday
                    .get(Calendar.DAY_OF_MONTH)
            }
            if (tomorrowPlans.isNotEmpty()) {
                total.add(HeaderItem(getString(R.string.tomorrow)))
                total.addAll(tomorrowPlans)
            }

            val authorDiffUtilCallback = AuthorDiffUtilCallback(adapter.items, total)
            val authorDiffResult = DiffUtil.calculateDiff(authorDiffUtilCallback)
            adapter.items = total
            authorDiffResult.dispatchUpdatesTo(adapter)
        })

        recycler_view.adapter = adapter
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT.or(ItemTouchHelper.RIGHT)
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                return if (viewHolder is PlansAdapter.HeaderViewHolder) {
                    val dragFlags = 0
                    val swipeFlags = 0
                    ItemTouchHelper.Callback.makeMovementFlags(
                        dragFlags,
                        swipeFlags
                    )
                } else {
                    val dragFlags = 0//ItemTouchHelper.UP or ItemTouchHelper.DOWN
                    val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
                    ItemTouchHelper.Callback.makeMovementFlags(
                        dragFlags,
                        swipeFlags
                    )
                }
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                planViewModel!!.delete(adapter.getPlanByPosition(viewHolder.adapterPosition), true)
                Toast.makeText(baseContext, "Note Deleted!", Toast.LENGTH_SHORT).show()
            }
        }
        ).attachToRecyclerView(recycler_view)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.delete_all_notes -> {
                planViewModel!!.deleteAll()
                Toast.makeText(this, "All notes deleted!", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.go_to_stats -> {
                val intent = Intent(applicationContext, StatActivity::class.java)
                startActivity(intent)
                true
            }
            else -> {
                false
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            Toast.makeText(this, "Note not saved!", Toast.LENGTH_SHORT).show()
        } else if (resultCode == Activity.RESULT_OK) {
            val plan = Plan(
                data!!.getStringExtra(AddingActivity.EXTRA_TITLE),
                data.getStringExtra(AddingActivity.EXTRA_NOTE),
                data.getLongExtra(AddingActivity.EXTRA_DATE, Calendar.getInstance().timeInMillis),
                data.getStringExtra(AddingActivity.EXTRA_LOCATION),
                data.getIntExtra(AddingActivity.EXTRA_PRIORITY, 1),
                data.getStringExtra(AddingActivity.EXTRA_CATEGORY)
            )

            if (requestCode == ADD_NOTE_REQUEST) {
                setTimeNotification(plan.date!!, plan.title!!, Date(plan.date))
                planViewModel!!.insert(plan)
            } else if (requestCode == EDIT_NOTE_REQUEST) {
                val id = data.getIntExtra(AddingActivity.EXTRA_ID, -1)
                if (id == -1) {
                    Toast.makeText(this, "Could not update! Error!", Toast.LENGTH_SHORT).show()
                    return
                }
                plan.id = id
                planViewModel!!.update(plan)
            }
        }
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = descriptionText
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun setTimeNotification(timeInMillis: Long, title: String, date: Date) {
        val intent = Intent(this, ReminderBroadcaster::class.java)
        intent.putExtra(AddingActivity.EXTRA_TITLE, title)
        val calendar = Calendar.getInstance()
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        calendar.time = date
        val planDay = calendar.get(Calendar.DAY_OF_MONTH)

        if (planDay == currentDay) intent.putExtra(EXTRA_CHECK_DAY, getString(R.string.today))
        else intent.putExtra(EXTRA_CHECK_DAY, getString(R.string.tomorrow))

        intent.putExtra(AddingActivity.EXTRA_TITLE, title)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
    }

    override fun onDestroy() {
        PlannerDatabase.destroyInstance()
        super.onDestroy()
    }

}
