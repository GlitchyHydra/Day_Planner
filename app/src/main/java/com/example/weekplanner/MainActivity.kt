package com.example.weekplanner

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weekplanner.data.Plan
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    companion object {
        const val ADD_NOTE_REQUEST = 101
        const val EDIT_NOTE_REQUEST = 102
    }

    var planViewModel: PlanViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonAddNote.setOnClickListener {
            startActivityForResult(Intent(this, AddingActivity::class.java)
                , ADD_NOTE_REQUEST)
        }

        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.setHasFixedSize(true)

        val adapter = PlannerAdapter()

        //ViewModelProvider.NewInstanceFactory().create(PlanViewModel::class.java)
        planViewModel = ViewModelProvider(this, PlanViewModelFactory(application)).get(PlanViewModel::class.java)
        planViewModel!!.getAllPlanes().observe(this, Observer<List<Plan>> {
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show()
            adapter.submitList(it)
        })

        recycler_view.adapter = adapter
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0,
            ItemTouchHelper.LEFT.or(ItemTouchHelper.RIGHT)) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                planViewModel!!.delete(adapter.getPlanByPosition(viewHolder.adapterPosition))
                Toast.makeText(baseContext, "Note Deleted!", Toast.LENGTH_SHORT).show()
            }
        }
        ).attachToRecyclerView(recycler_view)

        adapter.setOnItemClickListener(object : PlannerAdapter.OnItemClickListener {
            override fun onItemClick(plan: Plan) {
                var intent = Intent(baseContext, AddingActivity::class.java)
                intent.putExtra(AddingActivity.EXTRA_ID, plan.id)
                intent.putExtra(AddingActivity.EXTRA_TITLE, plan.title)
                intent.putExtra(AddingActivity.EXTRA_NOTE, plan.note)
                intent.putExtra(AddingActivity.EXTRA_DATE, plan.date)
                intent.putExtra(AddingActivity.EXTRA_LOCATION, plan.location)
                intent.putExtra(AddingActivity.EXTRA_PRIORITY, plan.priority)

                startActivityForResult(intent, EDIT_NOTE_REQUEST)
            }
        })
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
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ADD_NOTE_REQUEST && resultCode == Activity.RESULT_OK) {
            val newPlan = Plan(
                data!!.getStringExtra(AddingActivity.EXTRA_TITLE),
                data.getStringExtra(AddingActivity.EXTRA_NOTE),
                data.getStringExtra(AddingActivity.EXTRA_DATE),
                data.getStringExtra(AddingActivity.EXTRA_LOCATION),
                data.getIntExtra(AddingActivity.EXTRA_PRIORITY, 1)
            )
            planViewModel!!.insert(newPlan)

            Toast.makeText(this, "Note saved!", Toast.LENGTH_SHORT).show()
        } else if (requestCode == EDIT_NOTE_REQUEST && resultCode == Activity.RESULT_OK) {
            val id = data?.getIntExtra(AddingActivity.EXTRA_ID, -1)

            if (id == -1) {
                Toast.makeText(this, "Could not update! Error!", Toast.LENGTH_SHORT).show()
            }

            val updateNote = Plan(
                data!!.getStringExtra(AddingActivity.EXTRA_TITLE),
                data.getStringExtra(AddingActivity.EXTRA_NOTE),
                data.getStringExtra(AddingActivity.EXTRA_DATE),
                data.getStringExtra(AddingActivity.EXTRA_LOCATION),
                data.getIntExtra(AddingActivity.EXTRA_PRIORITY, 1)
            )
            updateNote.id = data.getIntExtra(AddingActivity.EXTRA_ID, -1)
            planViewModel!!.update(updateNote)

        } else {
            Toast.makeText(this, "Note not saved!", Toast.LENGTH_SHORT).show()
        }


    }

}
