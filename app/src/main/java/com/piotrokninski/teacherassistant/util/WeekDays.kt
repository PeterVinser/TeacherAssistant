package com.piotrokninski.teacherassistant.util

enum class WeekDays {
    MONDAY {
        override fun toString(): String {
            return "Monday"
        }
           },
    TUESDAY {
        override fun toString(): String {
            return "Tuesday"
        }
            },
    WEDNESDAY {
        override fun toString(): String {
            return "Wednesday"
        }
             },
    THURSDAY {
        override fun toString(): String {
            return "Thursday"
        }
             },
    FRIDAY {
        override fun toString(): String {
            return "Friday"
        }
           },
    SATURDAY {
        override fun toString(): String {
            return "Saturday"
        }
             },
    SUNDAY {
        override fun toString(): String {
            return "Sunday"
        }
    }

}