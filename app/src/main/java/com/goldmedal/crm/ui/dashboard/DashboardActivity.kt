package com.goldmedal.crm.ui.dashboard

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.goldmedal.crm.R
import com.goldmedal.crm.data.model.GetTicketsCountData
import com.goldmedal.crm.databinding.ActivityDashboardBinding
import com.goldmedal.crm.ui.auth.LoginActivity
import com.goldmedal.crm.ui.auth.LoginViewModel
import com.goldmedal.crm.ui.auth.LoginViewModelFactory
import com.goldmedal.crm.ui.auth.UserProfileActivity
import com.goldmedal.crm.ui.dashboard.home.AppointmentsActivity
import com.goldmedal.crm.ui.dashboard.leftpanel.ProgressReportActivity
import com.goldmedal.crm.ui.dashboard.notification.NotificationActivity
import com.goldmedal.crm.ui.invoice.InvoiceListActivity
import com.goldmedal.crm.ui.ticket.AcceptedTicketsActivity
import com.goldmedal.crm.ui.ticket.ServiceTicketActivity
import com.goldmedal.crm.ui.ticket.TicketHistoryActivity
import kotlinx.android.synthetic.main.activity_dashboard.*

import kotlinx.android.synthetic.main.nav_header_home_screen.*
import kotlinx.android.synthetic.main.toolbar.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance


class DashboardActivity : AppCompatActivity(), KodeinAware {

    override val kodein by kodein()
    private val factory: LoginViewModelFactory by instance()


    var mToast: Toast? = null

    companion object {
        private lateinit var viewModel: LoginViewModel
        private lateinit var navController: NavController


    }

    @SuppressLint("ShowToast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val binding: ActivityDashboardBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_dashboard)

        viewModel =
            ViewModelProvider(this, factory).get(LoginViewModel::class.java)

        binding.viewmodel = viewModel

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)


        mToast =
            Toast.makeText(this@DashboardActivity, R.string.press_back_again, Toast.LENGTH_SHORT)

        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()



        navController = Navigation.findNavController(this, R.id.navFragment)


        //  bottomNav.setupWithNavController(navController)
        navigationView.setupWithNavController(navController)




//        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment


//        val navController = navHostFragment.navController


        val navHostFragment = navFragment as NavHostFragment
        val inflater = navController.navInflater
        val graph = inflater.inflate(R.navigation.nav_graph)
        navController = navHostFragment.navController




        toolbar_notification?.setOnClickListener {
            startActivity(Intent(this, NotificationActivity::class.java))
        }


        viewModel.getLoggedInUser().observe(this, Observer { user ->
            if (user != null) {
                textViewMsg?.text = "Hello, " + user.UserName


                navigationView.getMenu().clear();


//                val isTrue = true
                if (user.RoleName.equals("Service Engineer", true)) {

                    graph.startDestination = R.id.homeFragment
                    navigationView.inflateMenu(R.menu.activity_nav_drawer);
                    bindServiceEngMenus()

                } else {

                    graph.startDestination = R.id.ManagerHomeFragment
                    navigationView.inflateMenu(R.menu.nav_drawer_manager);
                    bindServiceEngMenus()

                }
//                navController.setGraph(graph, intent.extras)
                navController.graph = graph


                Glide.with(this)
                    .load(user.ProfilePhoto)
                    .fitCenter()
                    .placeholder(R.drawable.male_avatar)
                    .into(imageViewProfile)

            }
        })

    }

    private fun bindServiceEngMenus() {

        val assignedTicketItem = navigationView.menu.findItem(R.id.actionAssignedTicket)
        val serviceTicketItem = navigationView.menu.findItem(R.id.actionServiceTicket)
        val ticketHistoryItem = navigationView.menu.findItem(R.id.actionTicketHistory)
        val upcomingAppointmentsItem = navigationView.menu.findItem(R.id.actionUpcomingAppointments)
        val progressReportItem = navigationView.menu.findItem(R.id.actionProgressReport)
        val invoiceList = navigationView.menu.findItem(R.id.actionInvoiceList)
        val logoutItem = navigationView.menu.findItem(R.id.actionLogout)
        val headerView = navigationView.getHeaderView(0)

        headerView.setOnClickListener {
            UserProfileActivity.start(this)
        }

        invoiceList.setOnMenuItemClickListener {
            InvoiceListActivity.start(this)
            true
        }

        assignedTicketItem.setOnMenuItemClickListener {
            //   logout()
            ServiceTicketActivity.start(this, GetTicketsCountData(1, "Accepted Tickets", 0))
            true
        }

        serviceTicketItem.setOnMenuItemClickListener {
            AcceptedTicketsActivity.start(this, 1)
            true
        }

        ticketHistoryItem.setOnMenuItemClickListener {
            TicketHistoryActivity.start(this)
            true
        }

        upcomingAppointmentsItem.setOnMenuItemClickListener {
            AppointmentsActivity.start(this)
            true
        }


        progressReportItem.setOnMenuItemClickListener {
            ProgressReportActivity.start(this)
            true
        }

        logoutItem.setOnMenuItemClickListener {
            logout()
            true
        }
    }

    private fun logout() {
        viewModel.logoutUser()
        Intent(this, LoginActivity::class.java).also {
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(it)
        }
        finish()
    }


    override fun onBackPressed() {


        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {


            if (navController.currentDestination?.id == R.id.homeFragment) {
                if (mToast?.view?.isShown == false) {
                    mToast?.show()
                    return
                } else {
                    //Exit App
                    super.onBackPressed()
                }

            }
            //Move back to home
            super.onBackPressed()

        }
    }


    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, drawerLayout)
    }


}