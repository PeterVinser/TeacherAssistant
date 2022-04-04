package com.piotrokninski.teacherassistant.view.main.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.piotrokninski.teacherassistant.databinding.HeaderListItemBinding
import com.piotrokninski.teacherassistant.databinding.MeetingListItemBinding
import com.piotrokninski.teacherassistant.model.adapteritem.CalendarAdapterItem
import java.lang.IllegalArgumentException
import java.util.*
import kotlin.collections.ArrayList

class CalendarAdapter(
    private val context: Context,
    private val linearLayoutManager: LinearLayoutManager,
    private val scrollListener: (Date) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val TAG = "CalendarAdapter"

    private val calendarItems = ArrayList<CalendarAdapterItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            CALENDAR_HEADER_ITEM -> {
                HeaderViewHolder(HeaderViewHolder.initBinding(parent), context)
            }
            CALENDAR_MEETING_ITEM -> {
                MeetingViewHolder(MeetingViewHolder.initBinding(parent))
            }
            else -> {
                throw IllegalArgumentException("Unknown viewType")
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            CALENDAR_HEADER_ITEM -> (holder as HeaderViewHolder).bind(calendarItems[position] as CalendarAdapterItem.HeaderAdapterItem)

            CALENDAR_MEETING_ITEM -> (holder as MeetingViewHolder).bind(calendarItems[position] as CalendarAdapterItem.MeetingAdapterItem)
        }

        val firstItemPosition = linearLayoutManager.findFirstCompletelyVisibleItemPosition()
        if (firstItemPosition > 0) {
            scrollListener(calendarItems[firstItemPosition].date)
        }
    }

    override fun getItemCount(): Int {
        return calendarItems.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (calendarItems[position]) {
            is CalendarAdapterItem.HeaderAdapterItem -> CALENDAR_HEADER_ITEM
            is CalendarAdapterItem.MeetingAdapterItem -> CALENDAR_MEETING_ITEM
        }
    }

    fun setCalendarItems(meetings: ArrayList<CalendarAdapterItem>) {
        this.calendarItems.clear()
        this.calendarItems.addAll(meetings)
        notifyDataSetChanged()
        scrollListener(meetings[0].date)
    }

    fun moveToDate(date: Date) {
        val position =
            calendarItems.indexOfFirst { it.date >= date && it is CalendarAdapterItem.HeaderAdapterItem}
        linearLayoutManager.scrollToPositionWithOffset(position, 0)
        scrollListener(calendarItems[position].date)
    }

    class MeetingViewHolder(private val binding: MeetingListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun initBinding(parent: ViewGroup): MeetingListItemBinding {
                val layoutInflater = LayoutInflater.from(parent.context)
                return MeetingListItemBinding.inflate(
                    layoutInflater,
                    parent,
                    false
                )
            }

            private const val TAG = "CalendarAdapter"
        }

        fun bind(calendarMeetingItem: CalendarAdapterItem.MeetingAdapterItem) {
            binding.meeting = calendarMeetingItem.meeting
        }
    }

    class HeaderViewHolder(
        private val binding: HeaderListItemBinding,
        private val context: Context
    ) : RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun initBinding(parent: ViewGroup): HeaderListItemBinding {
                val layoutInflater = LayoutInflater.from(parent.context)
                return HeaderListItemBinding.inflate(
                    layoutInflater,
                    parent,
                    false
                )
            }
        }

        fun bind(calendarHeaderItem: CalendarAdapterItem.HeaderAdapterItem) {
            binding.headerItemTitle.text = context.getString(calendarHeaderItem.titleId)
        }
    }

    companion object {
        const val CALENDAR_HEADER_ITEM = 0
        const val CALENDAR_MEETING_ITEM = 1
    }
}