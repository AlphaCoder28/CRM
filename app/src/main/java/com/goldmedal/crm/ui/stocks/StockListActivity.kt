package com.goldmedal.crm.ui.stocks

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldmedal.crm.R
import com.goldmedal.crm.common.ApiStageListener
import com.goldmedal.crm.data.model.GetInvoiceListData
import com.goldmedal.crm.data.model.StockItemData
import com.goldmedal.crm.databinding.ActivityStockListBinding
import com.goldmedal.crm.ui.invoice.InvoiceListActivity
import com.goldmedal.crm.ui.invoice.InvoiceListItem
import com.goldmedal.crm.ui.ticket.TicketViewModel
import com.goldmedal.crm.ui.ticket.TicketViewModelFactory
import com.goldmedal.crm.util.Coroutines
import com.goldmedal.crm.util.snackbar
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class StockListActivity : AppCompatActivity(), KodeinAware, ApiStageListener<Any> {
    override val kodein by kodein()
    private val factory: TicketViewModelFactory by instance()
    private lateinit var viewModel: TicketViewModel
    private lateinit var mBinding: ActivityStockListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityStockListBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        viewModel = ViewModelProvider(this, factory).get(TicketViewModel::class.java)
        viewModel.apiListener = this

        viewModel.getLoggedInUser().observe(this, Observer { user ->
            if (user != null) {
                user.UserId?.let { viewModel.getStockList(it) }
            }
        })
    }

    private fun List<StockItemData?>.toStockListItem(): List<StockListItem?> {
        return this.map {
            StockListItem(it,  this@StockListActivity)
        }
    }

    private fun bindUI(list: List<StockItemData?>?) = Coroutines.main {
        list?.let {
            initRecyclerView(it.toStockListItem())
        }
    }

    override fun onStarted(callFrom: String) {
        mBinding.viewCommon.showProgressBar()
    }

    override fun onError(message: String, callFrom: String, isNetworkError: Boolean) {
        mBinding.viewCommon.hide()
        if(isNetworkError){
            mBinding.viewCommon.showNoInternet()
        }else{
            mBinding.viewCommon.showServerError()
        }
        mBinding.root.snackbar(message)
    }

    override fun onValidationError(message: String, callFrom: String) {
        mBinding.root.snackbar(message)
    }

    override fun onSuccess(_object: List<Any?>, callFrom: String) {
        mBinding.viewCommon.hide()
        val data = _object as List<StockItemData?>
        if (data.isEmpty()) {
            mBinding.viewCommon.showNoData()
        }
        bindUI(data)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun initRecyclerView(toStockItem: List<StockListItem?>) {
        val mAdapter = GroupAdapter<GroupieViewHolder>().apply {
            addAll(toStockItem)
        }

        mBinding.rvStockList.apply {
            layoutManager = LinearLayoutManager(this@StockListActivity)
            setHasFixedSize(true)
            adapter = mAdapter
        }
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, StockListActivity::class.java))
        }
    }

}