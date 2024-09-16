package com.goldmedal.crm.ui.customers

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldmedal.crm.common.DashboardApiListener
import com.goldmedal.crm.data.model.ContactsData
import com.goldmedal.crm.databinding.ActivityContactsBinding
import com.goldmedal.crm.ui.dashboard.home.HomeViewModel
import com.goldmedal.crm.ui.dashboard.home.HomeViewModelFactory
import com.goldmedal.crm.util.Coroutines
import com.goldmedal.crm.util.snackbar
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.util.*

class ContactsActivity : AppCompatActivity(), KodeinAware, DashboardApiListener<Any> {

    override val kodein by kodein()

    private val factory: HomeViewModelFactory by instance()

    private lateinit var viewModel: HomeViewModel

    private lateinit var binding: ActivityContactsBinding

    var strSearchBy: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)
        viewModel.apiListener = this

        viewModel.getLoggedInUser().observe(this, Observer { user ->
            if (user != null) {
                viewModel.getCustomerContacts(user.UserId,strSearchBy)
            }
        })


        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                strSearchBy = newText
                if (newText.trim { it <= ' ' }.isEmpty() || newText.trim { it <= ' ' }.length > 1) {
                    viewModel.getLoggedInUser()
                        .observe(this@ContactsActivity, Observer { user ->

                            if (user != null) {
                                viewModel.getCustomerContacts(
                                    user.UserId,
                                    strSearchBy
                                )
                            }
                        })
                }
                return true
            }
        })
        binding.searchView.setOnCloseListener {
            strSearchBy = ""
            viewModel.getLoggedInUser().observe(this, Observer { user ->

                if (user != null) {
                    viewModel.getCustomerContacts(
                        user.UserId,
                        strSearchBy
                    )
                }
            })
            false
        }
    }



    private fun List<ContactsData?>.toContacts(): List<ContactsItem?> {
        return this.map {
            ContactsItem(it,this@ContactsActivity)
        }
    }


    private fun bindUI(list: List<ContactsData?>?) = Coroutines.main {
        list?.let {
            initRecyclerView(it.toContacts())
        }


    }

    private fun initRecyclerView(toLeaveRecord: List<ContactsItem?>) {
        val mAdapter = GroupAdapter<GroupieViewHolder>().apply {
            addAll(toLeaveRecord)
        }

        binding.rvList.apply {
            layoutManager = LinearLayoutManager(this@ContactsActivity)
            setHasFixedSize(true)
            adapter = mAdapter
        }
    }


    override fun onStarted(callFrom: String) {
        binding.viewCommon.showProgressBar()
    }

    override fun onSuccess(_object: List<Any?>, callFrom: String, timestamp: String) {

        binding.viewCommon.hide()
        val data = _object as List<ContactsData?>
        if (data.isNullOrEmpty()) {
            binding.viewCommon.showNoData()
        }
        bindUI(data)
    }

    override fun onError(message: String, callFrom: String, isNetworkError: Boolean) {

        binding.viewCommon.hide()
        if(isNetworkError){
            binding.viewCommon.showNoInternet()
        }else{
            binding.viewCommon.showServerError()
        }
        binding.rootLayout.snackbar(message)
    }

    override fun onValidationError(message: String, callFrom: String) {
        binding.rootLayout.snackbar(message)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, ContactsActivity::class.java))
        }
    }


}