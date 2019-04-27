package com.mileskrell.playdate.ui

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import java.text.SimpleDateFormat
import java.util.*

class DatePickerDialogFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    companion object {
        // The ID of the TextView that will have its text changed
        const val VIEW_ID = "id"
        val displayFormat = SimpleDateFormat("MMM. d, yyyy", Locale.US)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(context!!, this, year, month, day)
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        val viewId = arguments!!.getInt(VIEW_ID)

        val date = Calendar.getInstance().apply {
            set(year, month, day)
        }.time

        activity?.findViewById<TextView>(viewId)?.text = displayFormat.format(date)
    }
}