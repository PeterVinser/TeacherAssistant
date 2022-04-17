package com.piotrokninski.teacherassistant.util

import com.piotrokninski.teacherassistant.R

enum class WeekDays(val id: Int, val label: String, val buttonId: Int, val stringId: Int) {
    MONDAY(1, "Monday", R.id.week_date_picker_monday, R.string.monday_full) {
        override fun toString(): String {
            return label
        }
           },
    TUESDAY(2, "Tuesday", R.id.week_date_picker_tuesday, R.string.tuesday_full) {
        override fun toString(): String {
            return label
        }
            },
    WEDNESDAY(3, "Wednesday", R.id.week_date_picker_wednesday, R.string.wednesday_full) {
        override fun toString(): String {
            return label
        }
             },
    THURSDAY(4, "Thursday", R.id.week_date_picker_thursday, R.string.thursday_full) {
        override fun toString(): String {
            return label
        }
             },
    FRIDAY(5, "Friday", R.id.week_date_picker_friday, R.string.friday_full) {
        override fun toString(): String {
            return label
        }
           },
    SATURDAY(6, "Saturday", R.id.week_date_picker_saturday, R.string.saturday_full) {
        override fun toString(): String {
            return label
        }
             },
    SUNDAY(7, "Sunday", R.id.week_date_picker_sunday, R.string.sunday_full) {
        override fun toString(): String {
            return label
        }
    }

}