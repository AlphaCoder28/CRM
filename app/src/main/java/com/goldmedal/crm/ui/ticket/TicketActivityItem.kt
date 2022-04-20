package com.goldmedal.crm.ui.ticket

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.github.vipulasri.timelineview.TimelineView
import com.goldmedal.crm.R
import com.goldmedal.crm.data.model.TicketActivityData
import com.goldmedal.crm.util.getDrawable

import java.util.*

class TicketActivityItem(private val ticketActivity: List<TicketActivityData?>,private val ticketStatus: String) :
    RecyclerView.Adapter<TicketActivityItem.TimeLineViewHolder>() {

    private lateinit var mLayoutInflater: LayoutInflater

    override fun getItemViewType(position: Int): Int {
        return TimelineView.getTimeLineViewType(position, itemCount)
    }


    // override fun getItemCount() = ticketActivity.size

    override fun getItemCount(): Int {

        if(ticketStatus.toLowerCase().equals("reschedule")){
            return ticketActivity.size
        }else{
            return (ticketActivity.size - 1)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeLineViewHolder {

        if (!::mLayoutInflater.isInitialized) {
            mLayoutInflater = LayoutInflater.from(parent.context)
        }

        return TimeLineViewHolder(
            mLayoutInflater.inflate(
                R.layout.tab_activity_item,
                parent,
                false
            ), viewType
        )
    }



    override fun onBindViewHolder(holder: TimeLineViewHolder, position: Int) {
        val timeLineModel = ticketActivity[position]

        holder.apply {
            date.text = timeLineModel?.ActivityDateTime
            message.text = timeLineModel?.Activity

            when {
                timeLineModel?.Activity?.toLowerCase(Locale.getDefault())
                    ?.contains("visit") == true -> {
                    setMarker(holder, R.drawable.activity_site_visit, R.color.colorPrimary)
                }
                timeLineModel?.Activity?.toLowerCase(Locale.getDefault())
                    ?.contains("assign") == true -> {
                    setMarker(holder, R.drawable.activity_assigned, R.color.colorPrimary)
                }
                timeLineModel?.Activity?.toLowerCase(Locale.getDefault())
                    ?.contains("create") == true -> {
                    setMarker(holder, R.drawable.activity_ticket, R.color.colorPrimary)
                }
                else -> {
                    setMarker(holder, R.drawable.activity_status, R.color.colorPrimary)
                }

            }
        }
    }



        inner class TimeLineViewHolder(itemView: View, viewType: Int) :
            RecyclerView.ViewHolder(itemView) {

            var date: TextView =
                itemView.findViewById(R.id.text_timeline_date)//itemView.text_timeline_date
            var message: TextView =
                itemView.findViewById(R.id.text_timeline_title) //= itemView.text_timeline_title
            var timeline: TimelineView =
                itemView.findViewById(R.id.timeline) //= //itemView.timeline


            init {
                timeline.initLine(viewType)

//                timeline.initLine(viewType)
//                timeline.markerSize = 15
//                timeline.setMarkerColor(mAttributes.markerColor)
//                timeline.isMarkerInCenter = true
//                timeline.markerPaddingLeft = 0
//                timeline.markerPaddingTop = 12
//                timeline.markerPaddingRight = 12
//                timeline.markerPaddingBottom = 10
//                timeline.linePadding = 10
//
//                timeline.lineWidth = 2
//                timeline.setStartLineColor(R.color.colorPrimary, viewType)
//                timeline.setEndLineColor(R.color.colorPrimary, viewType)
//                timeline.lineStyle =
//                timeline.lineStyleDashLength = mAttributes.lineDashWidth
//                timeline.lineStyleDashGap = mAttributes.lineDashGap

            }
        }

        private fun setMarker(holder: TimeLineViewHolder, drawableResId: Int, colorFilter: Int) {
            holder.timeline.marker = getDrawable(
                holder.itemView.context,
                drawableResId,
                ContextCompat.getColor(holder.itemView.context, colorFilter)
            )
        }
    }
