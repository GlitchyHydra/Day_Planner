package com.example.weekplanner

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.adding_layout.*

class AddingActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_ID = "com.week_planner.java.EXTRA_ID"
        const val EXTRA_TITLE = "com.week_planner.java.EXTRA_TITLE"
        const val EXTRA_NOTE = "com.week_planner.java.EXTRA_NOTE"
        const val EXTRA_DATE = "com.week_planner.java.EXTRA_DATE"
        const val EXTRA_LOCATION = "com.week_planner.java.EXTRA_LOCATION"
        const val EXTRA_PRIORITY = "com.week_planner.java.EXTRA_PRIORITY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.adding_layout)

        number_picker_priority.minValue = 1
        number_picker_priority.maxValue = 10

        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)

        if (intent.hasExtra(EXTRA_ID)) {
            title = "Edit Note"
            edit_text_title.setText(intent.getStringExtra(EXTRA_TITLE))
            edit_text_note.setText(intent.getStringExtra(EXTRA_NOTE))
            number_picker_priority.value = intent.getIntExtra(EXTRA_PRIORITY, 1)
        } else {
            title = "Add Note"
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_plan_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.save_note -> {
                saveNote()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveNote() {
        if (edit_text_title.text.toString().trim().isBlank() ||
            edit_text_note.text.toString().trim().isBlank()) {
            Toast.makeText(this, "Can not insert empty note!", Toast.LENGTH_SHORT).show()
            return
        }

        val data = Intent().apply {
            putExtra(EXTRA_TITLE, edit_text_title.text.toString())
            putExtra(EXTRA_NOTE, edit_text_note.text.toString())
            putExtra(EXTRA_PRIORITY, number_picker_priority.value)
            if (intent.getIntExtra(EXTRA_ID, -1) != -1) {
                putExtra(EXTRA_ID, intent.getIntExtra(EXTRA_ID, -1))
            }
        }

        setResult(Activity.RESULT_OK, data)
        finish()
    }
}