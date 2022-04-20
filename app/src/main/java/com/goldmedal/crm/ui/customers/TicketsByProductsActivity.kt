package com.goldmedal.crm.ui.customers

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldmedal.crm.common.ApiStageListener
import com.goldmedal.crm.data.model.TicketsByProductsData
import com.goldmedal.crm.databinding.ActivityTicketsByProductsBinding
import com.goldmedal.crm.ui.ticket.TicketViewModel
import com.goldmedal.crm.ui.ticket.TicketViewModelFactory
import com.goldmedal.crm.util.Coroutines
import com.goldmedal.crm.util.snackbar
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.util.ArrayList

class TicketsByProductsActivity : AppCompatActivity(), KodeinAware, ApiStageListener<Any> {

    override val kodein by kodein()
    private val factory: TicketViewModelFactory by instance()
    private lateinit var viewModel: TicketViewModel
    private lateinit var binding: ActivityTicketsByProductsBinding

    private var customerId: Int = -1
    private var productId: Int = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_tickets_by_products)

        binding = ActivityTicketsByProductsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        viewModel = ViewModelProvider(this, factory).get(TicketViewModel::class.java)
        viewModel.apiListener = this

        intent?.let {
            customerId = it.getIntExtra(ARG_CUSTOMER_ID, -1)
            productId  =  it.getIntExtra(ARG_PRODUCT_ID, -1)
            supportActionBar?.setTitle(it.getStringExtra(ARG_CUSTOMER_NAME))
        }


        viewModel.getLoggedInUser().observe(this, Observer { user ->

            if (user != null) {
                viewModel.getTicketsByProducts(customerId, productId)
            }
        })


    }

  //RecyclerView Data source
  private fun List<TicketsByProductsData?>.toData(): List<TicketsByProductsItem?> {
      return this.map {
          TicketsByProductsItem(it, this@TicketsByProductsActivity)
      }
  }

    private fun initRecyclerView(data: List<TicketsByProductsItem?>) {
        val mAdapter = GroupAdapter<GroupieViewHolder>().apply {
            addAll(data)
        }

        binding.rvList.apply {
            layoutManager = LinearLayoutManager(this@TicketsByProductsActivity)
//            setHasFixedSize(false)
            //addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = mAdapter
        }
    }

    private fun bindUI(list: List<TicketsByProductsData?>?) = Coroutines.main {
        list?.let {
            initRecyclerView(it.toData())
        }
    }




//Functions
    override fun onStarted(callFrom: String) {
        binding.viewCommon.showProgressBar()
    }

    override fun onSuccess(_object: List<Any?>, callFrom: String) {
        binding.viewCommon.hide()

        bindUI(_object as List<TicketsByProductsData?>)

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
        private const val ARG_CUSTOMER_NAME = "customer_name"
        private const val ARG_CUSTOMER_ID = "customer_id"
        private const val ARG_PRODUCT_ID = "product_id"
        fun start(context: Context,customerName: String?,customerId:Int?,productId:Int?) {
            context.startActivity(Intent(context, TicketsByProductsActivity::class.java)
                .putExtra(ARG_CUSTOMER_NAME,customerName)
                .putExtra(ARG_CUSTOMER_ID,customerId)
                .putExtra(ARG_PRODUCT_ID,productId)
            )
        }
    }


}