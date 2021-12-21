package com.piotrokninski.teacherassistant.util

enum class WeekDays {
    MONDAY {
        override fun toString(): String {
            return "Poniedziałek"
        }
           },
    TUESDAY {
        override fun toString(): String {
            return "Wtorek"
        }
            },
    WEDNESDAY {
        override fun toString(): String {
            return "Środa"
        }
             },
    THURSDAY {
        override fun toString(): String {
            return "Czwartek"
        }
             },
    FRIDAY {
        override fun toString(): String {
            return "Piątek"
        }
           },
    SATURDAY {
        override fun toString(): String {
            return "Sobota"
        }
             },
    SUNDAY {
        override fun toString(): String {
            return "Niedziela"
        }
    }

}