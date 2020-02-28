package com.example.weekplanner

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weekplanner.data.Plan
import com.example.weekplanner.data.PlannerDatabase

class PlannerAdapter: ListAdapter<Plan, PlanViewHolder>(DIFF_CALLBACK){

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Plan>() {
            override fun areItemsTheSame(oldItem: Plan, newItem: Plan): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Plan, newItem: Plan): Boolean {
                return oldItem.title == newItem.title && oldItem.note == newItem.note
                        && oldItem.priority == newItem.priority
            }
        }
    }

    private var listener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(plan: Plan)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    fun getPlanByPosition(position: Int): Plan = getItem(position)

    /*override fun getItemViewType(position: Int): Int {
        return bibDatabase.getEntry(position % countOfEntries).type.ordinal
    }*/

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanViewHolder =
        PlanViewHolder.from(parent)

    override fun onBindViewHolder(holder: PlanViewHolder, position: Int) {
        val plan = getItem(position)
        holder.bind(plan)
        /*when (note.type) {

        }*/
    }
}

class PlanViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    companion object {
        fun from(parent: ViewGroup) : PlanViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.plan, parent, false)
            return PlanViewHolder(itemView)
        }
    }

    fun bind(plan: Plan) {
        titleView.text = plan.title
        noteView.text = plan.note
        dateView.text = plan.date
        //locationView.text = plan.location
        priorityView.text = plan.priority.toString()
    }

    val titleView: TextView
    val noteView: TextView
    val dateView: TextView
    //val locationView: TextView
    val priorityView: TextView

    init {
        titleView = view.findViewById(R.id.text_view_title)
        noteView = view.findViewById(R.id.text_view_note)
        dateView = view.findViewById(R.id.text_view_date)
        //locationView = view.findViewById(R.id.text_view_location)
        priorityView = view.findViewById(R.id.text_view_priority)
    }
}