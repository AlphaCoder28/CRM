package com.goldmedal.crm.ui.parts

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldmedal.crm.common.ApiStageListener
import com.goldmedal.crm.data.model.SelectPartsListData
import com.goldmedal.crm.databinding.AvailablePartsLayoutBinding
import com.goldmedal.crm.util.Coroutines
import com.goldmedal.crm.util.snackbar
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_used_parts.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class AvailablePartsActivity : AppCompatActivity(), KodeinAware, ApiStageListener<Any> {

    override val kodein by kodein()

    private val factory: PartsViewModelFactory by instance()

    private lateinit var viewModel: PartsViewModel

    private lateinit var binding: AvailablePartsLayoutBinding

    var strSearchBy: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AvailablePartsLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        viewModel = ViewModelProvider(this, factory).get(PartsViewModel::class.java)
        viewModel.apiListener = this

        viewModel.getLoggedInUser().observe(this, Observer { user ->
            if (user != null) {
                viewModel.getStockPartsList(user.UserId ?: 0,"0",strSearchBy)
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
                        .observe(this@AvailablePartsActivity, Observer { user ->

                            if (user != null) {
                                viewModel.getStockPartsList(
                                    user.UserId ?: 0,
                                    "0",
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
                    viewModel.getStockPartsList(
                        user.UserId ?: 0,
                        "0",
                        strSearchBy
                    )
                }
            })
            false
        }
    }



    private fun List<SelectPartsListData?>.availableParts(): List<AvailablePartsItemRow?> {
        return this.map {
            AvailablePartsItemRow(it,this@AvailablePartsActivity)
        }
    }


    private fun bindUI(list: List<SelectPartsListData?>?) = Coroutines.main {
        list?.let {
            initRecyclerView(it.availableParts())
        }


    }

    private fun initRecyclerView(availableParts: List<AvailablePartsItemRow?>) {
        val mAdapter = GroupAdapter<GroupieViewHolder>().apply {
            addAll(availableParts)
        }

        binding.rvAvailableParts.apply {
            layoutManager = LinearLayoutManager(this@AvailablePartsActivity)
            setHasFixedSize(true)
            adapter = mAdapter
        }
    }


    override fun onStarted(callFrom: String) {
        binding.viewCommonAvailableParts.showProgressBar()
    }

    override fun onSuccess(_object: List<Any?>, callFrom: String) {

        binding.viewCommonAvailableParts.hide()
        val data = _object as List<SelectPartsListData?>
        if (data.isNullOrEmpty()) {
            binding.viewCommonAvailableParts.showNoData()
        }
        bindUI(data)
    }

    override fun onError(message: String, callFrom: String, isNetworkError: Boolean) {

        binding.viewCommonAvailableParts.hide()
        if(isNetworkError){
            binding.viewCommonAvailableParts.showNoInternet()
        }else{
            binding.viewCommonAvailableParts.showServerError()
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
            context.startActivity(Intent(context, AvailablePartsActivity::class.java))
        }
    }
    
}