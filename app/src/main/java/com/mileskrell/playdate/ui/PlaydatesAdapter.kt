package com.mileskrell.playdate.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.mileskrell.playdate.R
import com.mileskrell.playdate.model.Playdate
import kotlinx.android.synthetic.main.playdate_list_item.view.*
import java.text.SimpleDateFormat
import java.util.*

class PlaydatesAdapter: RecyclerView.Adapter<PlaydatesAdapter.PlaydateViewHolder>() {

    private var playdates: List<Playdate> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaydateViewHolder {
        return PlaydateViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.playdate_list_item, parent, false))
    }

    override fun getItemCount(): Int = playdates.size

    override fun onBindViewHolder(holder: PlaydateViewHolder, position: Int) {
        holder.setUpForPlaydate(playdates[position])
    }

    fun loadPlaydates(playdates: List<Playdate>) {
        this.playdates = playdates
        notifyDataSetChanged()
    }

    class PlaydateViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun setUpForPlaydate(playdate: Playdate) {

            itemView.playdate_name_text_view.text = playdate.name

            if (playdate.startTime != null) {
                // See https://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html
                val sourceDateTime = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.US).parse(playdate.startTime)
                val startString = SimpleDateFormat("MMM. d, h:mm a", Locale.US).format(sourceDateTime)

                itemView.playdate_when_text_view.text = startString
                itemView.playdate_when_text_view
                    .setTextColor(ContextCompat.getColor(itemView.context, android.R.color.tab_indicator_text))
            } else {
                itemView.playdate_when_text_view.text = itemView.context.getString(R.string.pending)
                itemView.playdate_when_text_view
                    .setTextColor(ContextCompat.getColor(itemView.context, android.R.color.holo_red_dark))
            }

            itemView.setOnClickListener {
                itemView.findNavController()
                    .navigate(PlaydateListFragmentDirections.actionGoToPlaydateDetailsDest(playdate.code))
            }
        }
    }
}
