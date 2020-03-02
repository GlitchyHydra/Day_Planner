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
import java.util.Calendar
import java.util.Date


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

    private var radioGroup: RadioGroup? = null
    private var tomorrowRadio: RadioButton? = null

    private val choosedDateAndTime = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.adding_layout)

        radioGroup = findViewById(R.id.radioGroup)
        tomorrowRadio = findViewById(R.id.tomorrowRadio)

        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)

        val timePickerText = findViewById<TextView>(R.id.editTime)
        timePickerText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
            val currentMinute = calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(
                this,
                OnTimeSetListener { timePicker, hourOfDay, minutes ->
                    timePickerText.text = String.format("%02d:%02d", hourOfDay, minutes)
                    choosedDateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    choosedDateAndTime.set(Calendar.MINUTE, minutes)
                }, currentHour, currentMinute, true
            )

            timePickerDialog.show()
        }

        if (intent.hasExtra(EXTRA_ID)) {
            title = "Edit Note"
            edit_text_title.setText(intent.getStringExtra(EXTRA_TITLE))
            edit_text_note.setText(intent.getStringExtra(EXTRA_NOTE))
            choosedDateAndTime.time = Date(intent.getLongExtra(EXTRA_DATE, 0))
            val hourOfDay = choosedDateAndTime.get(Calendar.HOUR_OF_DAY)
            val minutes = choosedDateAndTime.get(Calendar.MINUTE)
            editTime.text = String.format("%02d:%02d", hourOfDay, minutes)
            val calendarToday = Calendar.getInstance()
            tomorrowRadio?.isChecked =
                calendarToday.get(Calendar.DAY_OF_MONTH) != choosedDateAndTime.get(Calendar.DAY_OF_MONTH)
            val button = radioGroup?.getChildAt(intent.getIntExtra(EXTRA_PRIORITY, 0))
            (button as RadioButton).isChecked = true
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
            edit_text_note.text.toString().trim().isBlank()
        ) {
            Toast.makeText(this, "Can not insert empty note!", Toast.LENGTH_SHORT).show()
            return
        }

        if (tomorrowRadio!!.isChecked) {
            choosedDateAndTime.add(Calendar.DAY_OF_YEAR, 1)
        }

        val radioButtonID = radioGroup!!.checkedRadioButtonId
        val radioButton = radioGroup!!.findViewById<RadioButton>(radioButtonID)
        val idx = radioGroup!!.indexOfChild(radioButton)

        val data = Intent().apply {
            putExtra(EXTRA_TITLE, edit_text_title.text.toString())
            putExtra(EXTRA_NOTE, edit_text_note.text.toString())
            putExtra(EXTRA_PRIORITY, idx)
            putExtra(EXTRA_DATE, choosedDateAndTime.timeInMillis)

            if (intent.getIntExtra(EXTRA_ID, -1) != -1) {
                putExtra(
                    EXTRA_ID, intent.getIntExtra(
                        EXTRA_ID, -1
                    )
                )
            }
        }

        setResult(Activity.RESULT_OK, data)
        finish()
    }
}