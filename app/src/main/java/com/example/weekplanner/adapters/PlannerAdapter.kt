package com.example.weekplanner.adapters

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weekplanner.R
import com.example.weekplanner.data.Plan
import java.util.Collections.emptyList
import com.example.weekplanner.adapters.ListItem
import java.time.Month
import java.util.*

class AuthorDiffUtilCallback(private val oldList: List<ListItem>, private val newList: List<ListItem>): DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int)
            = true//oldList[oldItemPosition].plan.id == newList[newItemPosition].plan.id
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int)
            = oldList[oldItemPosition] == newList[newItemPosition]
}
/*
class PlannerAdapter: ListAdapter<Plan, PlanViewHolder>(
    DIFF_CALLBACK
){

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
*/
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
        dateView.text = Date(plan.date!!).toString()
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

fun getNextDay(): Calendar {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, 1)
    return calendar
}


class PlansAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private class HeaderViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var txt_header: TextView

        init {
            txt_header = itemView.findViewById(R.id.txt_header)
        }
    }

    private class PlanViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val titleView: TextView
        val noteView: TextView
        val dateView: TextView
        //val locationView: TextView
        val priorityView: TextView

        init {
            titleView = itemView.findViewById(R.id.text_view_title)
            noteView = itemView.findViewById(R.id.text_view_note)
            dateView = itemView.findViewById(R.id.text_view_date)
            //locationView = itemView.findViewById(R.id.text_view_location)
            priorityView = itemView.findViewById(R.id.text_view_priority)
        }
    }

    var items: List<ListItem> = listOf(HeaderItem(Calendar.getInstance().time),
        HeaderItem(getNextDay().time))

    fun getPlanByPosition(position: Int): Plan = (items[position] as PlanItem).plan

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            ListItem.TYPE_HEADER -> {
                val itemView: View =
                    inflater.inflate(R.layout.view_list_header, parent, false)
                HeaderViewHolder(itemView)
            }
            ListItem.TYPE_EVENT -> {
                val itemView: View =
                    inflater.inflate(R.layout.plan, parent, false)
                PlanViewHolder(itemView)
            }
            else -> throw IllegalStateException("unsupported item type")
        }
    }

    override fun onBindViewHolder(
        viewHolder: RecyclerView.ViewHolder,
        position: Int
    ) {
        when (getItemViewType(position)) {
            ListItem.TYPE_HEADER -> {
                val header: HeaderItem = items[position] as HeaderItem
                val holder = viewHolder as HeaderViewHolder

                val calendar = Calendar.getInstance()
                calendar.time = header.date
                val day = calendar.get(Calendar.DAY_OF_MONTH)
                val month = calendar.get(Calendar.MONTH)

                holder.txt_header.text = String.format("%02d %s", day, Month.values()[month])
            }
            ListItem.TYPE_EVENT -> {
                val planItem: PlanItem = items[position] as PlanItem
                val holder =
                    viewHolder as PlanViewHolder

                holder.titleView.text = planItem.plan.title
                holder.noteView.text = planItem.plan.note
                holder.dateView.text = Date(planItem.plan.date!!).toString()
                //locationView.text = plan.location
                holder.priorityView.text = planItem.plan.priority.toString()
            }
            else -> throw IllegalStateException("unsupported item type")
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return items[position].getType()
    }

}