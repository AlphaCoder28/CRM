package com.goldmedal.crm.ui.customers

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldmedal.crm.common.ApiStageListener
import com.goldmedal.crm.data.model.CustomerProductsData
import com.goldmedal.crm.databinding.ActivityProductsBinding
import com.goldmedal.crm.ui.ticket.TicketViewModel
import com.goldmedal.crm.ui.ticket.TicketViewModelFactory
import com.goldmedal.crm.util.Coroutines
import com.goldmedal.crm.util.snackbar
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.util.*

private const val ARG_PARAM = "customer_id"
private const val CUSTOMER_NAME = "customer_name"

class CustomerProductsActivity : AppCompatActivity(), KodeinAware, ApiStageListener<Any> {

    override val kodein by kodein()
    private val factory: TicketViewModelFactory by instance()
    private lateinit var viewModel: TicketViewModel
    private lateinit var binding: ActivityProductsBinding
    var strSearchBy: String = ""
        private var customerId: Int = -1
        private var customerName: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProductsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        viewModel = ViewModelProvider(this, factory).get(TicketViewModel::class.java)
        viewModel.apiListener = this

        intent?.let {
            customerId = it.getIntExtra(ARG_PARAM, -1)
            customerName = it.getStringExtra(CUSTOMER_NAME)
            supportActionBar?.setTitle(customerName)
        }


        binding.searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                strSearchBy = newText
                //  filter(newText);
                if (newText.trim { it <= ' ' }.isEmpty() || newText.trim { it <= ' ' }.length > 1) {
                    // currentPage = 0
                    // newArrDispatchedMaterial.clear()

                    viewModel.getLoggedInUser()
                        .observe(this@CustomerProductsActivity, Observer { user ->

                            if (user != null) {
                                viewModel.getCustomerProducts(customerId, strSearchBy)
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
                    viewModel.getCustomerProducts(customerId, strSearchBy)
                }
            })
            false
        }


        viewModel.getLoggedInUser().observe(this, Observer { user ->

            if (user != null) {
                viewModel.getCustomerProducts(customerId, strSearchBy)
            }
        })
    }

    override fun onStarted(callFrom: String) {

        binding.viewCommon.showProgressBar()

    }

    override fun onSuccess(_object: List<Any?>, callFrom: String) {


        binding.viewCommon.hide()

        bindUI(_object as List<CustomerProductsData?>)

    }

    private fun List<CustomerProductsData?>.toData(): List<CustomerProductsItem?> {
        return this.map {
            CustomerProductsItem(it, this@CustomerProductsActivity,customerId,customerName)
        }
    }

    private fun initRecyclerView(data: List<CustomerProductsItem?>) {
        val mAdapter = GroupAdapter<GroupieViewHolder>().apply {
            addAll(data)
        }

        binding.rvList.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = mAdapter
        }
    }

    private fun bindUI(list: List<CustomerProductsData?>?) = Coroutines.main {
        list?.let {
            initRecyclerView(it.toData())
        }
    }

    override fun onError(message: String, callFrom: String, isNetworkError: Boolean) {
        if (isNetworkError) {
            binding.viewCommon.showNoInternet()
        } else {
            binding.viewCommon.showNoData()
        }

        bindUI(ArrayList())
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
        fun start(context: Context,customerName: String?,customerId: Int?) {
            context.startActivity(Intent(context, CustomerProductsActivity::class.java)
                .putExtra(CUSTOMER_NAME, customerName)
                .putExtra(ARG_PARAM, customerId))
        }
    }

}