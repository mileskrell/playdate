package com.mileskrell.playdate.ui

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.TooltipCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.snackbar.Snackbar
import com.mileskrell.playdate.R
import com.mileskrell.playdate.model.UserViewModel
import kotlinx.android.synthetic.main.fragment_playdate_list.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class PlaydateListFragment : Fragment(), CoroutineScope {

    lateinit var userViewModel: UserViewModel

    val job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + job

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_playdate_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        userViewModel = ViewModelProviders.of(activity!!).get(UserViewModel::class.java)

        if (!userViewModel.accountIsInitialized()) {
            val acct = GoogleSignIn.getLastSignedInAccount(context)
            if (acct == null) {
                if (findNavController().currentDestination?.id != R.id.login_dest) {
                    findNavController().navigate(R.id.action_go_to_login_dest)
                }
            } else {
                // The user's info only gets saved here, even if they logged in in LoginFragment
                userViewModel.storeUserAndInitRepo(acct)

                playdates_list_progress.visibility = View.VISIBLE
                new_playdate_fab.isEnabled = false
                join_playdate_fab.isEnabled = false
                launch(Dispatchers.IO) {
                    try {
                        userViewModel.sendUser()
                        userViewModel.getPlaydates()
                        activity?.runOnUiThread {
                            new_playdate_fab.isEnabled = true
                            join_playdate_fab.isEnabled = true
                        }
                    } catch (e: RuntimeException) {
                        Snackbar.make(view, getString(R.string.network_error), Snackbar.LENGTH_LONG).show()
                    } finally {
                        activity?.runOnUiThread {
                            playdates_list_progress.visibility = View.INVISIBLE
                        }
                    }
                }
            }
        }

        // TODO Add ability to log out without closing app

        playdates_recycler_view.setHasFixedSize(true)
        playdates_recycler_view.layoutManager = LinearLayoutManager(context)
        val adapter = PlaydatesAdapter()
        playdates_recycler_view.adapter = adapter

        TooltipCompat.setTooltipText(new_playdate_fab, getString(R.string.new_playdate))
        TooltipCompat.setTooltipText(join_playdate_fab, getString(R.string.join_a_playdate))

        userViewModel.playdates.observe(this, Observer {
            adapter.loadPlaydates(it)
            if (it.isEmpty()) {
                playdates_recycler_view.visibility = View.INVISIBLE
                no_playdates_text_view.visibility = View.VISIBLE
            } else {
                no_playdates_text_view.visibility = View.INVISIBLE
                playdates_recycler_view.visibility = View.VISIBLE
            }
        })

        playdates_recycler_view.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    new_playdate_fab.hide()
                    join_playdate_fab.hide()
                } else {
                    new_playdate_fab.show()
                    join_playdate_fab.show()
                }
            }
        })

        new_playdate_fab.setOnClickListener {
            findNavController().navigate(R.id.action_go_to_new_playdate_dest)
        }

        join_playdate_fab.setOnClickListener {
            JoinPlaydateDialogFragment { code ->
                launch(Dispatchers.IO) {
                    val availability = userViewModel.getAvailability(code)
                    activity?.runOnUiThread {
                        if (availability != null) {
                            // TODO We've gotten availability; now do something!
                            Snackbar.make(view, "Successfully joined playdate; now we should let user select time.", Snackbar.LENGTH_SHORT).show()
                        } else {
                            Snackbar.make(view, getString(R.string.joining_playdate_failed), Snackbar.LENGTH_SHORT).show()
                        }
                    }
                }
            }.show(fragmentManager!!, null)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.list_playdates_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.button_log_out -> {
                userViewModel.logOut()
                activity?.finish()
                true
            }
            R.id.button_clear_playdates -> {
                userViewModel.debugClearPlaydates()
                true
            }
            else -> false
        }
    }
}
