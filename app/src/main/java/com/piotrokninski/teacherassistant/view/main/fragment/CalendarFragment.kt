package com.piotrokninski.teacherassistant.view.main.fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.piotrokninski.teacherassistant.R
class CalendarFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }
}