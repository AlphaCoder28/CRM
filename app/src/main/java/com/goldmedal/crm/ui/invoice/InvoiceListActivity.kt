package com.goldmedal.crm.ui.invoice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldmedal.crm.common.ApiStageListener
import com.goldmedal.crm.data.model.ContactsData
import com.goldmedal.crm.data.model.GetInvoiceListData
import com.goldmedal.crm.databinding.ActivityInvoiceListBinding
import com.goldmedal.crm.ui.ticket.TicketViewModel
import com.goldmedal.crm.ui.ticket.TicketViewModelFactory
import com.goldmedal.crm.util.Coroutines
import com.goldmedal.crm.util.snackbar
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_accepted_ticket.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class InvoiceListActivity : AppCompatActivity(), KodeinAware, ApiStageListener<Any> {

    override val kodein by kodein()
    private val factory: TicketViewModelFactory by instance()
    private lateinit var viewModel: TicketViewModel
    private lateinit var binding: ActivityInvoiceListBinding
    private var invoiceListData: GetInvoiceListData? = null
    var strSearchBy: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInvoiceListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)


        viewModel = ViewModelProvider(this, factory).get(TicketViewModel::class.java)
        viewModel.apiListener = this


        viewModel.getLoggedInUser().observe(this, Observer { user ->
            if (user != null) {
                viewModel.getInvoiceListDetail(user.UserId ?: 0,strSearchBy)
            }
        })
        search_view?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                strSearchBy = newText
                if (newText.trim { it <= ' ' }.isEmpty() || newText.trim { it <= ' ' }.length > 1) {
                    viewModel.getLoggedInUser()
                        .observe(this@InvoiceListActivity, Observer { user ->

                            if (user != null) {
                                viewModel.getInvoiceListDetail(
                                    user.UserId ?: 0,
                                    strSearchBy
                                )
                            }
                        })
                }
                return true
            }
        })
        search_view?.setOnCloseListener {
            strSearchBy = ""
            viewModel.getLoggedInUser().observe(this, Observer { user ->

                if (user != null) {
                    viewModel.getInvoiceListDetail(
                        user.UserId ?: 0,
                        strSearchBy
                    )
                }
            })
            false
        }
    }

    private fun List<GetInvoiceListData?>.toInvoiceListItem(): List<InvoiceListItem?> {
        return this.map {
            InvoiceListItem(it,  this@InvoiceListActivity)
        }
    }


    private fun bindUI(list: List<GetInvoiceListData?>?) = Coroutines.main {
        list?.let {
            if (it != null) {
                initRecyclerView(it.toInvoiceListItem())
            }

        }
    }

    override fun onStarted(callFrom: String) {
        binding.viewCommon.showProgressBar()
    }

    override fun onSuccess(_object: List<Any?>, callFrom: String) {

        binding.viewCommon.hide()
        val data = _object as List<GetInvoiceListData?>
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


    private fun initRecyclerView(toAddedInvoiceItem: List<InvoiceListItem?>) {
        val mAdapter = GroupAdapter<GroupieViewHolder>().apply {
            addAll(toAddedInvoiceItem)
        }

        binding.rvInvoiceHistoryList.apply {
            layoutManager = LinearLayoutManager(this@InvoiceListActivity)
            setHasFixedSize(true)
            adapter = mAdapter
        }
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, InvoiceListActivity::class.java))
        }
    }
}

