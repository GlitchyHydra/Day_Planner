package com.example.weekplanner.adapters

abstract class ListItem {

    companion object {
        val TYPE_HEADER = 0
        val TYPE_EVENT = 1
    }

    abstract fun getType(): Int
}