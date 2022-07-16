package com.piotrokninski.teacherassistant.view.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.piotrokninski.teacherassistant.R
import com.piotrokninski.teacherassistant.databinding.HeaderListItemBinding
import com.piotrokninski.teacherassistant.databinding.MeetingListItemBinding
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

    private val calendarItems = ArrayList<Item>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            Item.Header.ID -> {
                HeaderViewHolder(HeaderViewHolder.initBinding(parent), context)
            }
            Item.Meeting.ID -> {
                MeetingViewHolder(MeetingViewHolder.initBinding(parent))
            }
            else -> {
                throw IllegalArgumentException("Unknown viewType")
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            Item.Header.ID -> (holder as HeaderViewHolder)
                .bind(calendarItems[position] as Item.Header)

            Item.Meeting.ID -> (holder as MeetingViewHolder)
                .bind(calendarItems[position] as Item.Meeting)
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
            is Item.Header -> Item.Header.ID
            is Item.Meeting -> Item.Meeting.ID
        }
    }

    fun setCalendarItems(meetings: ArrayList<Item>) {
        this.calendarItems.clear()
        this.calendarItems.addAll(meetings)
        notifyDataSetChanged()
        scrollListener(meetings[0].date)
    }

    fun moveToDate(date: Date) {
        val position =
            calendarItems.indexOfFirst { it.date >= date && it is Item.Header }
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

        fun bind(meetingItem: Item.Meeting) {
            binding.meeting = meetingItem.meeting
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

        fun bind(headerItem: Item.Header) {
            binding.headerItemTitle.text = context.getString(headerItem.titleId)
        }
    }


    sealed class Item {

        abstract val id: String
        abstract val date: Date

        data class Header(val titleId: Int, override val date: Date): Item() {
            override val id = titleId.toString()

            companion object {
                fun getHeaders(): Map<Int, Int> {
                    return mapOf(
                        Calendar.MONDAY to R.string.monday_full,
                        Calendar.TUESDAY to R.string.tuesday_full,
                        Calendar.WEDNESDAY to R.string.wednesday_full,
                        Calendar.THURSDAY to R.string.thursday_full,
                        Calendar.FRIDAY to R.string.friday_full,
                        Calendar.SATURDAY to R.string.saturday_full,
                        Calendar.SUNDAY to R.string.sunday_full
                    )
                }

                const val ID = 0
            }
        }

        data class Meeting(val meeting: com.piotrokninski.teacherassistant.model.Meeting): Item() {
            override val id = meeting.date.toString()
            override val date = meeting.date!!

            companion object {
                const val ID = 1
            }
        }
    }
}