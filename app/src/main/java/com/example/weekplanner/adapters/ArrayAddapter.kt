package com.example.weekplanner.adapters

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Spinner

fun createArrayAdapter(context: Context, textArrayResId: Int, spinner: Spinner?) {
    ArrayAdapter.createFromResource(
        context,
        textArrayResId,
        android.R.layout.simple_spinner_item
    ).also { adapter ->
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Apply the adapter to the spinner
        spinner?.adapter = adapter
    }
}