package com.mileskrell.playdate.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.mileskrell.playdate.R

class MainActivity : AppCompatActivity() {

    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navController = findNavController(R.id.nav_host_fragment)
        setupActionBarWithNavController(navController, AppBarConfiguration(setOf(R.id.login_dest, R.id.playdate_list_dest)))
    }

    override fun onBackPressed() {
        onSupportNavigateUp()
    }

    override fun onSupportNavigateUp(): Boolean {
        if (navController.currentDestination?.id == R.id.login_dest
            || navController.currentDestination?.id == R.id.playdate_list_dest
            || navController.currentDestination?.id == null) {
            finish()
            return true
        }

        // If going back from "new playdated created" page, pop the back stack first to
        // skip over the "new playdate creation" page.
        //
        // It seems we have to do this here; including this in the the "new playdate created" action
        // makes it play the "new playdate creation" page's pop exit animation, which we don't want.
        if (navController.currentDestination?.id == R.id.new_playdate_created_dest) {
            navController.popBackStack()
        }

        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
