package com.example.weekplanner.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import android.app.Activity
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.weekplanner.R
import com.example.weekplanner.activities.AddingActivity
import com.example.weekplanner.activities.MainActivity.Companion.EDIT_NOTE_REQUEST
import com.example.weekplanner.data.Plan
import java.util.Collections.emptyList

class AuthorDiffUtilCallback(
    private val oldList: List<ListItem>,
    private val newList: List<ListItem>
) : DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        true//oldList[oldItemPosition].plan.id == newList[newItemPosition].plan.id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition] == newList[newItemPosition]
}

class PlansAdapter(
    private val colorNormal: Int,
    private val colorImportant: Int,
    private val colorVeryImportant: Int,
    private var activityContext: Context?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class HeaderViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var txt_header: TextView

        init {
            txt_header = itemView.findViewById(R.id.txt_header)
        }
    }

    class PlanViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val titleView: TextView
        val noteView: TextView
        //val locationView: TextView
        val priorityView: ImageView
        val planCardView: CardView

        init {
            titleView = itemView.findViewById(R.id.text_view_title)
            noteView = itemView.findViewById(R.id.text_view_note)
            //locationView = itemView.findViewById(R.id.text_view_location)
            priorityView = itemView.findViewById(R.id.imageViewPriority)
            planCardView = itemView.findViewById(R.id.plan_card_view)
        }
    }

    var items: List<ListItem> = emptyList()

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

                holder.txt_header.text = header.dateInString
            }
            ListItem.TYPE_EVENT -> {
                val planItem: PlanItem = items[position] as PlanItem
                val holder =
                    viewHolder as PlanViewHolder

                holder.titleView.text = planItem.plan.title
                holder.noteView.text = planItem.plan.note
                val plan = planItem.plan
                holder.planCardView.setOnClickListener {
                    val intent = Intent(activityContext, AddingActivity::class.java)
                    intent.putExtra(AddingActivity.EXTRA_ID, plan.id)
                    intent.putExtra(AddingActivity.EXTRA_TITLE, plan.title)
                    intent.putExtra(AddingActivity.EXTRA_NOTE, plan.note)
                    intent.putExtra(AddingActivity.EXTRA_DATE, plan.date)
                    intent.putExtra(AddingActivity.EXTRA_LOCATION, plan.location)
                    intent.putExtra(AddingActivity.EXTRA_PRIORITY, plan.priority)

                    (activityContext as Activity).startActivityForResult(intent, EDIT_NOTE_REQUEST)
                }
                //locationView.text = plan.location
                val color = when (planItem.plan.priority) {
                    0 -> colorNormal
                    1 -> colorImportant
                    2 -> colorVeryImportant
                    else -> colorNormal
                }
                holder.priorityView.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_IN)
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

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        activityContext = null
        super.onDetachedFromRecyclerView(recyclerView)
    }
}