package com.mileskrell.playdate.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.mileskrell.playdate.R
import com.mileskrell.playdate.model.UserViewModel
import com.mileskrell.playdate.util.hideSoftKeyboard
import kotlinx.android.synthetic.main.fragment_new_playdate.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*
import kotlin.coroutines.CoroutineContext

class NewPlaydateFragment : Fragment(), CoroutineScope {

    // To allow holding on to "X is blank" error messages after restoring state
    // (otherwise, the text change listeners would just clear them.
    // As seen in https://stackoverflow.com/a/45036261
    companion object {
        const val IGNORE_NEXT_CHANGES = "ignoreNextChanges"
    }

    var ignoreNextChanges = 0

    lateinit var userViewModel: UserViewModel

    val job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + job

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_new_playdate, container, false)
    }

    override fun onDestroyView() {
        hideSoftKeyboard(view)
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        userViewModel = ViewModelProviders.of(activity!!).get(UserViewModel::class.java)

        setUpInputListeners(view)
        displayInitialDates()

        setUpDateClickListeners()

        new_playdate_create_button.setOnClickListener {
            // If name is blank, display an error for that
            if (new_playdate_name_text_input_layout.error == null && new_playdate_name_edit_text.text!!.isEmpty()) {
                new_playdate_name_text_input_layout.error = getString(R.string.playdate_needs_a_name)
            }

            // If length is blank, display an error for that
            if (new_playdate_length_text_input_layout.error == null && new_playdate_length_edit_text.text!!.isEmpty()) {
                new_playdate_length_text_input_layout.error = getString(R.string.playdate_needs_a_length)
            }

            // If there are no errors, continue
            if (new_playdate_name_text_input_layout.error == null
                && new_playdate_length_text_input_layout.error == null
            ) {
                hideSoftKeyboard(view.findFocus())
                new_playdate_progress_bar.visibility = View.VISIBLE

                val startDay = DatePickerDialogFragment.displayFormat
                    .parse(new_playdate_start_date_text_view.text.toString())
                val endDay = DatePickerDialogFragment.displayFormat
                    .parse(new_playdate_end_date_text_view.text.toString())

                launch(Dispatchers.IO) {
                    val code = userViewModel.createPlaydate(
                        new_playdate_name_edit_text.text.toString(),
                        new_playdate_length_edit_text.text.toString().toInt(),
                        startDay, endDay
                    )
                    activity?.runOnUiThread {
                        new_playdate_progress_bar.visibility = View.INVISIBLE
                        findNavController().navigate(NewPlaydateFragmentDirections.actionGoToNewPlaydateCreatedDest(code))
                    }
                }
            }
        }

        savedInstanceState?.let {
            ignoreNextChanges = it.getInt(IGNORE_NEXT_CHANGES)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(IGNORE_NEXT_CHANGES, 2)
    }

    private fun setUpDateClickListeners() {
        new_playdate_start_date_text_view.setOnClickListener {
            DatePickerDialogFragment().apply {
                arguments = Bundle().apply {
                    putInt(DatePickerDialogFragment.VIEW_ID, R.id.new_playdate_start_date_text_view)
                }
            }.show(fragmentManager!!, null)
        }

        new_playdate_end_date_text_view.setOnClickListener {
            DatePickerDialogFragment().apply {
                arguments = Bundle().apply {
                    putInt(DatePickerDialogFragment.VIEW_ID, R.id.new_playdate_end_date_text_view)
                }
            }.show(fragmentManager!!, null)
        }
    }

    private fun displayInitialDates() {
        val todayString = DatePickerDialogFragment.displayFormat.format(Date())
        val endString = DatePickerDialogFragment.displayFormat.format(Date(Date().time + 7 * 24 * 60 * 60 * 1000))

        new_playdate_start_date_text_view.text = todayString
        new_playdate_end_date_text_view.text = endString
    }

    private fun setUpInputListeners(view: View) {
        new_playdate_name_edit_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (ignoreNextChanges != 0) {
                    ignoreNextChanges--
                } else {
                    new_playdate_name_text_input_layout.run {
                        if (s!!.length > 128) {
                            // This check isn't just "error == null" because there are two possible errors
                            if (error != getString(R.string.playdate_name_too_long)) {
                                error = getString(R.string.playdate_name_too_long)
                            }
                        } else {
                            error = null
                        }
                    }
                }
            }
        })

        new_playdate_length_edit_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (ignoreNextChanges != 0) {
                    ignoreNextChanges--
                } else {
                    new_playdate_length_text_input_layout.run {
                        if (s.toString().toIntOrNull() == 0) {
                            // This check isn't just "error == null" because there are two possible errors
                            if (error != getString(R.string.playdate_cannot_be_0_minutes)) {
                                error = getString(R.string.playdate_cannot_be_0_minutes)
                            }
                        } else {
                            error = null
                        }
                    }
                }
            }
        })

        // Hide keyboard when user presses IME "done" or "next" button while in length EditText
        new_playdate_length_edit_text.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                hideSoftKeyboard(view.findFocus())
                // TODO Can we EVER reliably clear focus? Ugh. Not worth the time.
            }
            false
        }
    }
}
