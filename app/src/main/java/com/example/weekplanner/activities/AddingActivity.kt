package com.example.weekplanner.activities

import android.app.Activity
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.weekplanner.R
import kotlinx.android.synthetic.main.adding_layout.*
import java.util.*


class AddingActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_ID = "com.week_planner.java.EXTRA_ID"
        const val EXTRA_TITLE = "com.week_planner.java.EXTRA_TITLE"
        const val EXTRA_NOTE = "com.week_planner.java.EXTRA_NOTE"
        const val EXTRA_DATE = "com.week_planner.java.EXTRA_DATE"
        const val EXTRA_LOCATION = "com.week_planner.java.EXTRA_LOCATION"
        const val EXTRA_PRIORITY = "com.week_planner.java.EXTRA_PRIORITY"
        const val EXTRA_CATEGORY = "com.week_planner.java.EXTRA_CATEGORY"
    }

    private val currentTimeInMillis get() = Calendar.getInstance().time.time
    private var priority = 0
    private var spinner: Spinner? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.adding_layout)

        spinner = findViewById(R.id.prioritySpinner)

// Create an ArrayAdapter using the string array and a default spinner layout
        val spinnerAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.priority_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner?.adapter = adapter
        }

        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)

        findViewById<TextView>(R.id.editTime).setOnClickListener {
            val calendar = Calendar.getInstance()
            val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
            val currentMinute = calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(this,
                OnTimeSetListener { timePicker, hourOfDay, minutes ->
                    findViewById<TextView>(R.id.editTime).setText(String.format("%02d:%02d", hourOfDay, minutes))
                }, currentHour, currentMinute, false
            )

            timePickerDialog.show()
        }

        if (intent.hasExtra(EXTRA_ID)) {
            title = "Edit Note"
            edit_text_title.setText(intent.getStringExtra(EXTRA_TITLE))
            edit_text_note.setText(intent.getStringExtra(EXTRA_NOTE))
            spinner?.setSelection(intent.getIntExtra(EXTRA_PRIORITY, 0))
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
            putExtra(EXTRA_PRIORITY, spinner?.selectedItemPosition)
            dayPicker.text
            if (intent.getIntExtra(EXTRA_ID, -1) != -1) {
                putExtra(
                    EXTRA_ID, intent.getIntExtra(
                        EXTRA_ID, -1))
            }
        }

        setResult(Activity.RESULT_OK, data)
        finish()
    }
}