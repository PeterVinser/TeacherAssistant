package com.piotrokninski.teacherassistant.view.main.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.piotrokninski.teacherassistant.databinding.CalendarListItemBinding
import com.piotrokninski.teacherassistant.model.meeting.Meeting

class CalendarAdapter(private val context: Context): RecyclerView.Adapter<CalendarAdapter.MeetingViewHolder>() {
    private val TAG = "CalendarAdapter"

    private val meetings = ArrayList<Meeting>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeetingViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        val binding = CalendarListItemBinding.inflate(layoutInflater, parent, false)

        return MeetingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MeetingViewHolder, position: Int) {
        holder.bind(meetings[position])
    }

    override fun getItemCount(): Int {
        return meetings.size
    }

    fun setMeetings(meetings: ArrayList<Meeting>) {
        this.meetings.clear()
        this.meetings.addAll(meetings)
        this.meetings.forEach {
            Log.d(TAG, "setMeetings: ${it.subject}")
        }
        notifyDataSetChanged()
    }

    class MeetingViewHolder(private val binding: CalendarListItemBinding):
        RecyclerView.ViewHolder(binding.root) {

        fun bind(meeting: Meeting) {
            binding.meeting = meeting
        }
    }
}