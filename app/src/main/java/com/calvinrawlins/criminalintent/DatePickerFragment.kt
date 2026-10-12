package com.calvinrawlins.criminalintent

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.util.Calendar
import java.util.Date

class DatePickerFragment : DialogFragment() {

    override fun onCreateDialog(
        savedInstanceState: Bundle?
    ): Dialog {

        val date =
            Date(
                requireArguments()
                    .getLong(ARG_DATE)
            )

        val calendar =
            Calendar.getInstance().apply {
                time = date
            }

        val year =
            calendar.get(
                Calendar.YEAR
            )

        val month =
            calendar.get(
                Calendar.MONTH
            )

        val day =
            calendar.get(
                Calendar.DAY_OF_MONTH
            )

        return DatePickerDialog(
            requireContext(),
            { _, selectedYear,
              selectedMonth,
              selectedDay ->

                val selectedDate =
                    Calendar
                        .getInstance()
                        .apply {
                            set(
                                selectedYear,
                                selectedMonth,
                                selectedDay,
                                12,
                                0,
                                0
                            )

                            set(
                                Calendar.MILLISECOND,
                                0
                            )
                        }
                        .time

                parentFragmentManager
                    .setFragmentResult(
                        REQUEST_KEY,
                        Bundle().apply {
                            putLong(
                                BUNDLE_KEY_DATE,
                                selectedDate.time
                            )
                        }
                    )
            },
            year,
            month,
            day
        )
    }

    companion object {

        const val REQUEST_KEY =
            "datePickerRequest"

        const val BUNDLE_KEY_DATE =
            "selectedDate"

        private const val ARG_DATE =
            "date"

        fun newInstance(
            date: Date
        ): DatePickerFragment {

            return DatePickerFragment().apply {
                arguments =
                    Bundle().apply {
                        putLong(
                            ARG_DATE,
                            date.time
                        )
                    }
            }
        }
    }
}