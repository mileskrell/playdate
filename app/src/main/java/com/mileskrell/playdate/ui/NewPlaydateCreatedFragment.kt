package com.mileskrell.playdate.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.mileskrell.playdate.R
import kotlinx.android.synthetic.main.fragment_new_playdate_created.*

class NewPlaydateCreatedFragment : Fragment() {

    private val args: PlaydateDetailsFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_new_playdate_created, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        new_playdate_created_code_text_view.text = args.playdateCode
    }
}
