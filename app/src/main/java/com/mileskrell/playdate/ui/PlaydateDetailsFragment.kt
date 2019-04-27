package com.mileskrell.playdate.ui

import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.mileskrell.playdate.R
import com.mileskrell.playdate.model.Playdate
import com.mileskrell.playdate.model.UserViewModel
import kotlinx.android.synthetic.main.fragment_playdate_details.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.CoroutineContext

class PlaydateDetailsFragment : Fragment(), CoroutineScope {

    val args: PlaydateDetailsFragmentArgs by navArgs()
    lateinit var userViewModel: UserViewModel

    lateinit var playdate: Playdate

    val job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + job

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_playdate_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        userViewModel = ViewModelProviders.of(activity!!).get(UserViewModel::class.java)

        // TODO Should we use a hash table instead of a list?
        playdate = userViewModel.playdates.value!!.first { it.code == args.playdateCode }

        playdate_details_name_text_view.text = playdate.name

        playdate_details_code_text_view.append(getBoldSpannableString(playdate.code))

        if (playdate.startTime != null) {
            val sourceDateTime = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.US).parse(playdate.startTime)
            val startString = SimpleDateFormat("MMM. d, h:mm a", Locale.US).format(sourceDateTime)

            val endMillis = sourceDateTime.time + playdate.duration * 60_000
            val endString = SimpleDateFormat("MMM. d, h:mm a", Locale.US).format(Date(endMillis))

            playdate_details_start_text_view.append(getBoldSpannableString(startString))
            playdate_details_end_text_view.append(getBoldSpannableString(endString))

            playdate_details_start_text_view.visibility = View.VISIBLE
            playdate_details_end_text_view.visibility = View.VISIBLE
            playdate_details_pending_text_view.visibility = View.INVISIBLE

            reschedule_button.setOnClickListener {
                Snackbar.make(view, "Initiates rescheduling sequence", Snackbar.LENGTH_SHORT).show()
            }
        } else {
            playdate_details_start_text_view.visibility = View.INVISIBLE
            playdate_details_end_text_view.visibility = View.INVISIBLE
            playdate_details_pending_text_view.visibility = View.VISIBLE
            reschedule_button.visibility = View.GONE
            // It looks like we don't actually need the following line! But just for future reference:
//            (cancel_button.layoutParams as ConstraintLayout.LayoutParams).endToEnd = (cancel_button.parent as View).id
        }

        cancel_button.setOnClickListener {
            AlertDialog.Builder(context!!)
                .setMessage(getString(R.string.cancel_playdate_question))
                .setPositiveButton(getString(R.string.yes)) { _: DialogInterface, _: Int ->
                    playdate_details_progress_bar.visibility = View.VISIBLE
                    launch(Dispatchers.IO) {
                        val removed = userViewModel.cancelPlaydate(playdate)
                        activity?.runOnUiThread {
                            playdate_details_progress_bar.visibility = View.INVISIBLE
                            if (removed) {
                                findNavController().popBackStack()
                            } else {
                                Snackbar.make(view, getString(R.string.canceling_playdate_failed), Snackbar.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                .setNegativeButton(getString(R.string.no)) { _: DialogInterface, _: Int -> }
                .create()
                .show()
        }
    }

    private fun getBoldSpannableString(string: String): SpannableString {
        return SpannableString(string)
            .apply { setSpan(StyleSpan(Typeface.BOLD), 0, length, 0) }
    }
}
