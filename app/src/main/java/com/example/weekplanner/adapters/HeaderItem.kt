package com.example.weekplanner.adapters

import java.util.Date;

class HeaderItem(val date: Date) : ListItem() {

    override fun getType(): Int {
        return TYPE_HEADER
    }
}