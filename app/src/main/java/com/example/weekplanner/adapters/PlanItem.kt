package com.example.weekplanner.adapters

import com.example.weekplanner.data.Plan

class PlanItem(val plan: Plan) : ListItem() {
    override fun getType(): Int {
        return TYPE_EVENT
    }
}