package com.goldmedal.crm.ui.parts

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldmedal.crm.common.ApiStageListener
import com.goldmedal.crm.data.model.UsedItemAndPartData
import com.goldmedal.crm.data.model.UsedPartAndItemData
import com.goldmedal.crm.databinding.ActivityUsedPartsBinding
import com.goldmedal.crm.util.Coroutines
import com.goldmedal.crm.util.interfaces.UsedPartListener
import com.goldmedal.crm.util.snackbar
import com.goldmedal.hrapp.ui.dialogs.UsedItemsDialog
import com.goldmedal.hrapp.ui.dialogs.UsedPartDialog
import com.google.android.material.tabs.TabLayout
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_used_parts.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class UsedPartsActivity : AppCompatActivity(), KodeinAware, ApiStageListener<Any>,
    UsedPartListener {

    override val kodein by kodein()

    private val factory: PartsViewModelFactory by instance()

    private lateinit var viewModel: PartsViewModel

    private lateinit var binding: ActivityUsedPartsBinding

    var strSearchBy: String = ""

    var tabPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsedPartsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        binding.layoutParts.root.visibility = View.GONE
        binding.layoutItems.root.visibility = View.VISIBLE

        initTabs()

        viewModel = ViewModelProvider(this, factory).get(PartsViewModel::class.java)
        viewModel.apiListener = this

        viewModel.getLoggedInUser().observe(this, Observer { user ->
            if (user != null) {
                viewModel.getItemAndPartDetail(0, strSearchBy)
                viewModel.getPartAndItemDetail(0, strSearchBy)
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
                        .observe(this@UsedPartsActivity, Observer { user ->

                            if (user != null) {
                                if (tabPosition == 0) {
                                    viewModel.getItemAndPartDetail(
                                        0,
                                        strSearchBy
                                    )
                                } else {
                                    viewModel.getPartAndItemDetail(
                                        0,
                                        strSearchBy
                                    )
                                }

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
                    if (tabPosition == 0) {
                        viewModel.getItemAndPartDetail(
                            0,
                            strSearchBy
                        )
                    } else {
                        viewModel.getPartAndItemDetail(
                            0,
                            strSearchBy
                        )
                    }

                }
            })
            false
        }
    }

    private fun initTabs() {
        binding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {

                search_view.setQuery("", false);
                search_view.clearFocus();
                strSearchBy = ""
                tabPosition = tab.position
                if (tabPosition == 0) {
                    // toast("position 0")
                    binding.layoutParts.root.visibility = View.GONE
                    binding.layoutItems.root.visibility = View.VISIBLE
                } else {
                    // toast("position 1")
                    binding.layoutParts.root.visibility = View.VISIBLE
                    binding.layoutItems.root.visibility = View.GONE
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }


    private fun List<UsedItemAndPartData?>.toItemAndPart(): List<UsedItemAndPartRow?> {
        return this.map {
            UsedItemAndPartRow(it, this@UsedPartsActivity, this@UsedPartsActivity)
        }
    }

    private fun List<UsedPartAndItemData?>.toPartAndItem(): List<UsedPartAndItemRow?> {
        return this.map {
            UsedPartAndItemRow(it, this@UsedPartsActivity, this@UsedPartsActivity)
        }
    }


    private fun bindItemsUI(list: List<UsedItemAndPartData?>?) = Coroutines.main {

        list?.let {
            initItemRecyclerView(it.toItemAndPart())
        }
    }

    private fun bindPartsUI(list: List<UsedPartAndItemData?>?) = Coroutines.main {

        list?.let {
            initPartRecyclerView(it.toPartAndItem())
        }
    }

    private fun initItemRecyclerView(toItemAndPartData: List<UsedItemAndPartRow?>) {
        val mAdapter = GroupAdapter<GroupieViewHolder>().apply {
            addAll(toItemAndPartData)
        }

        binding.layoutItems.rvItemsDetail.apply {
            layoutManager = LinearLayoutManager(this@UsedPartsActivity)
            setHasFixedSize(true)
            adapter = mAdapter
        }
    }

    private fun initPartRecyclerView(toPartAndItemData: List<UsedPartAndItemRow?>) {
        val mAdapter = GroupAdapter<GroupieViewHolder>().apply {
            addAll(toPartAndItemData)
        }

        binding.layoutParts.rvPartsDetail.apply {
            layoutManager = LinearLayoutManager(this@UsedPartsActivity)
            setHasFixedSize(true)
            adapter = mAdapter
        }
    }


    override fun onStarted(callFrom: String) {
        //binding.viewCommon.showProgressBar()
        if (callFrom.equals("itemAndPartDetail")) {
            binding.layoutItems.viewCommonItems.showProgressBar()
        }

        if (callFrom.equals("partAndItemDetail")) {
            binding.layoutParts.viewCommonParts.showProgressBar()
        }
    }

    override fun onSuccess(_object: List<Any?>, callFrom: String) {

        if (callFrom.equals("itemAndPartDetail")) {
            binding.layoutItems.viewCommonItems.hide()
            val data = _object as List<Any?>
            bindItemsUI(data as List<UsedItemAndPartData?>)

            if (data.isNullOrEmpty()) {
                binding.layoutItems.viewCommonItems.showNoData()
            }
        }

        if (callFrom.equals("partAndItemDetail")) {
            binding.layoutParts.viewCommonParts.hide()
            val data = _object as List<Any?>
            bindPartsUI(data as List<UsedPartAndItemData?>)

            if (data.isNullOrEmpty()) {
                binding.layoutParts.viewCommonParts.showNoData()
            }
        }
    }

    override fun onError(message: String, callFrom: String, isNetworkError: Boolean) {
        if (callFrom.equals("itemAndPartDetail")) {
            binding.layoutItems.viewCommonItems.hide()
            if (isNetworkError) {
                binding.layoutItems.viewCommonItems.showNoInternet()
            } else {
                binding.layoutItems.viewCommonItems.showServerError()
            }
        }

        if (callFrom.equals("partAndItemDetail")) {
            binding.layoutParts.viewCommonParts.hide()
            if (isNetworkError) {
                binding.layoutParts.viewCommonParts.showNoInternet()
            } else {
                binding.layoutParts.viewCommonParts.showServerError()
            }
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
            context.startActivity(Intent(context, UsedPartsActivity::class.java))
        }
    }

    override fun itemClicked(slno: Int?, strName: String?, callFrom: String) {
        if (callFrom.equals("ItemAndPart")) {
            val dialogFragment = UsedItemsDialog.newInstance()
            val bundle = Bundle()
            bundle.putInt("ItemSlNo", slno ?: 0)
            bundle.putString("ItemName", strName ?: "-")
            dialogFragment.arguments = bundle
            val ft = this.supportFragmentManager.beginTransaction()
            val prev = this.supportFragmentManager.findFragmentByTag("used_item_dialog")
            if (prev != null) {
                ft.remove(prev)
            }
            ft.addToBackStack(null)
            dialogFragment.show(ft, "used_item_dialog")
        }

        if (callFrom.equals("PartAndItem")) {
            val dialogFragment = UsedPartDialog.newInstance()
            val bundle = Bundle()
            bundle.putInt("PartSlNo", slno ?: 0)
            bundle.putString("PartName", strName ?: "-")
            dialogFragment.arguments = bundle
            val ft = this.supportFragmentManager.beginTransaction()
            val prev = this.supportFragmentManager.findFragmentByTag("used_part_dialog")
            if (prev != null) {
                ft.remove(prev)
            }
            ft.addToBackStack(null)
            dialogFragment.show(ft, "used_part_dialog")
        }

    }


}