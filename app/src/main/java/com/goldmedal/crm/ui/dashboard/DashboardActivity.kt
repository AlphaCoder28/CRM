package com.goldmedal.crm.ui.dashboard

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
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
import com.goldmedal.crm.BuildConfig
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
import com.goldmedal.crm.ui.stocks.StockListActivity
import com.goldmedal.crm.ui.ticket.AcceptedTicketsActivity
import com.goldmedal.crm.ui.ticket.ServiceTicketActivity
import com.goldmedal.crm.ui.ticket.TicketHistoryActivity
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.testing.FakeAppUpdateManager
import com.google.android.play.core.install.model.ActivityResult
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.tasks.Task
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.nav_header_home_screen.*
import kotlinx.android.synthetic.main.toolbar.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

private const val APP_UPDATE_TYPE_SUPPORTED = AppUpdateType.IMMEDIATE
private const val REQUEST_UPDATE = 100

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

                try {
                    Glide.with(this)
                        .load(user.ProfilePhoto)
                        .fitCenter()
                        .placeholder(R.drawable.male_avatar)
                        .into(imageViewProfile)
                } catch (e: Exception) {
                    e.printStackTrace()
                    imageViewProfile.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.male_avatar, null))
                }


            }
        })

        checkForUpdates()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (REQUEST_UPDATE == requestCode) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    if (APP_UPDATE_TYPE_SUPPORTED == AppUpdateType.IMMEDIATE) {
                        Toast.makeText(baseContext, "Updated Successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(baseContext, "Update Started", Toast.LENGTH_SHORT).show()
                    }
                }
                Activity.RESULT_CANCELED -> {
                    Toast.makeText(baseContext, "Update cancelled", Toast.LENGTH_SHORT).show()
                }
                ActivityResult.RESULT_IN_APP_UPDATE_FAILED -> {
                    Toast.makeText(baseContext, "Update Failed", Toast.LENGTH_SHORT).show()
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun checkForUpdates() {
        val appUpdateManager : AppUpdateManager
        /*if (BuildConfig.DEBUG) {
            appUpdateManager = FakeAppUpdateManager(baseContext)
            appUpdateManager.setUpdateAvailable(2)
        } else {*/
            appUpdateManager = AppUpdateManagerFactory.create(baseContext)
        //}
        val appUpdateInfo = appUpdateManager.appUpdateInfo
        appUpdateInfo.addOnSuccessListener {
            handleUpdate(appUpdateManager, appUpdateInfo)
        }
    }

    private fun handleUpdate(manager: AppUpdateManager, info: Task<AppUpdateInfo>) {
        if (APP_UPDATE_TYPE_SUPPORTED == AppUpdateType.IMMEDIATE) {
            handleImmediateUpdate(manager, info)
        } else if (APP_UPDATE_TYPE_SUPPORTED == AppUpdateType.FLEXIBLE) {
            //handleFlexibleUpdate(manager, info)
        }
    }

    private fun handleImmediateUpdate(manager: AppUpdateManager, info: Task<AppUpdateInfo>) {
        if ((info.result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE ||
                    info.result.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) &&
            info.result.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
        ) {
            manager.startUpdateFlowForResult(
                info.result,
                AppUpdateType.IMMEDIATE,
                this,
                REQUEST_UPDATE
            )
            if (BuildConfig.DEBUG) {
                val fakeAppUpdate = manager as FakeAppUpdateManager
                if (fakeAppUpdate.isImmediateFlowVisible) {
                    fakeAppUpdate.userAcceptsUpdate()
                    fakeAppUpdate.downloadStarts()
                    fakeAppUpdate.downloadCompletes()
                    launchRestartDialog(manager)
                }
            }
        }
    }

    private fun launchRestartDialog(manager: AppUpdateManager) {
        AlertDialog.Builder(this)
            .setTitle("App Update")
            .setMessage("Application Successfully updated! You need to restart the app.")
            .setPositiveButton("Restart") { _, _ ->
                manager.completeUpdate()
            }
            .create().show()
    }

    /*private fun handleFlexibleUpdate(manager: AppUpdateManager, info: Task<AppUpdateInfo>) {
        if ((info.result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE ||
                    info.result.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) &&
            info.result.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
            btn_update.visibility = View.VISIBLE
            setUpdateAction(manager, info)
        }
    }*/


    private fun bindServiceEngMenus() {

        val assignedTicketItem = navigationView.menu.findItem(R.id.actionAssignedTicket)
        val serviceTicketItem = navigationView.menu.findItem(R.id.actionServiceTicket)
        val ticketHistoryItem = navigationView.menu.findItem(R.id.actionTicketHistory)
        val upcomingAppointmentsItem = navigationView.menu.findItem(R.id.actionUpcomingAppointments)
        val progressReportItem = navigationView.menu.findItem(R.id.actionProgressReport)
        val invoiceList = navigationView.menu.findItem(R.id.actionInvoiceList)
        val stockList = navigationView.menu.findItem(R.id.actionStockList)
        val logoutItem = navigationView.menu.findItem(R.id.actionLogout)
        val headerView = navigationView.getHeaderView(0)

        headerView.setOnClickListener {
            UserProfileActivity.start(this)
        }

        invoiceList.setOnMenuItemClickListener {
            InvoiceListActivity.start(this)
            true
        }

        stockList.setOnMenuItemClickListener {
            StockListActivity.start(this)
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
