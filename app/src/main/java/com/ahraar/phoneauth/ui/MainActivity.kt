package com.ahraar.phoneauth.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.ahraar.phoneauth.R
import com.ahraar.phoneauth.databinding.ActivityMainBinding
import com.ahraar.phoneauth.utils.eventbus_events.ConnectionChangeEvent
import com.ahraar.phoneauth.utils.eventbus_events.KeyboardEvent
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MainActivity : AppCompatActivity() {

    private var isActivityRecreated = false
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setSupportActionBar(binding.toolbar)
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        appBarConfiguration =
                AppBarConfiguration(setOf(R.id.homeFragment, R.id.profileFragment))
        setupActionBar(navController, appBarConfiguration)

        setupBottomNavMenu(navController)

        //hide toolbar on signup,login fragments
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.loginFragment -> updateToolbarAndBottomNavigation(" ", View.GONE, View.GONE)
                R.id.OTPVerifyFragment -> updateToolbarAndBottomNavigation(
                        " ",
                        View.GONE,
                        View.GONE
                )
                R.id.homeFragment -> updateToolbarAndBottomNavigation(
                        "Home",
                        View.VISIBLE,
                        View.VISIBLE
                )
                R.id.profileFragment -> updateToolbarAndBottomNavigation(
                        "Profile",
                        View.VISIBLE,
                        View.VISIBLE
                )
                R.id.registerFragment -> updateToolbarAndBottomNavigation(
                    "Register",
                    View.VISIBLE,
                    View.GONE
                )

            }
        }
        //register to event bus to receive callbacks
        EventBus.getDefault().register(this)

        binding.bottomNavView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)


    }

    private fun updateToolbarAndBottomNavigation(
            title: String,
            toolbarVisibility: Int,
            visibility: Int
    ) {
        binding.toolbar.visibility = toolbarVisibility
        supportActionBar!!.title = title
        binding.bottomNavView.visibility = visibility

    }

    private fun setupBottomNavMenu(navController: NavController) {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav_view)
        bottomNav?.setupWithNavController(navController)
    }


    private fun setupActionBar(
            navController: NavController,
            appBarConfig: AppBarConfiguration
    ) {

        setupActionBarWithNavController(navController, appBarConfig)
    }

    // Show snackbar whenever the connection state changes
    @Subscribe(threadMode = ThreadMode.MAIN)
    open fun onConnectionChangeEvent(event: ConnectionChangeEvent) {
        if (!isActivityRecreated) {//to not show toast on configuration changes
            Snackbar.make(binding.mainLayout, event.message, Snackbar.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onKeyboardEvent(event: KeyboardEvent) {
        hideKeyboard()
    }


    private fun hideKeyboard() {

        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(binding.toolbar.windowToken, 0)

    }

    private fun isValidDestination(destination: Int): Boolean {
        return destination != Navigation.findNavController(this, R.id.nav_host_fragment)
                .currentDestination!!.id
    }

    private val mOnNavigationItemSelectedListener =
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.home -> {

                        val navOptions = NavOptions.Builder().setPopUpTo(R.id.nav_graph, true).build()
                        if (isValidDestination(R.id.homeFragment)) {
                            Navigation.findNavController(this, R.id.nav_host_fragment)
                                    .navigate(R.id.homeFragment, null, navOptions)
                        }
                    }

                    R.id.profile -> {

                        if (isValidDestination(R.id.profileFragment)) {
                            Navigation.findNavController(this, R.id.nav_host_fragment)
                                    .navigate(R.id.profileFragment)
                        }
                    }

                }
                item.isChecked = true
                false
            }


}