package com.goldmedal.hrapp.ui.dialogs


import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldmedal.crm.common.ApiStageListener
import com.goldmedal.crm.data.model.UsedItemAndPartData
import com.goldmedal.crm.databinding.DialogUsedItemsBinding
import com.goldmedal.crm.databinding.DialogUsedPartsBinding
import com.goldmedal.crm.ui.parts.PartsViewModel
import com.goldmedal.crm.ui.parts.PartsViewModelFactory
import com.goldmedal.crm.ui.parts.UsedItemAndPartDialogRow
import com.goldmedal.crm.util.Coroutines
import com.goldmedal.crm.util.toast
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.dialog_used_items.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

// - - - - Hit item API here and show part list, show item as header
class UsedItemsDialog : DialogFragment(), KodeinAware, ApiStageListener<Any> {

    override val kodein by kodein()

    private val factory: PartsViewModelFactory by instance()
    private lateinit var viewModel: PartsViewModel

    private lateinit var binding: DialogUsedPartsBinding

    var strSearchBy: String = ""
    var intItemSlNo = 0
    var strItemName = String()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        arguments?.let {
            intItemSlNo = arguments?.getInt("ItemSlNo") ?: 0
            strItemName = arguments?.getString("ItemName") ?: "-"
        }

        return dialog
    }

    override fun onResume() {
        super.onResume()

        val metrics = requireContext().resources.displayMetrics
        val screenWidth = (metrics.widthPixels * 0.85).toInt()
        dialog?.window?.setLayout(screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog?.window?.setGravity(Gravity.CENTER)
        dialog?.setCanceledOnTouchOutside(true)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        binding = DialogUsedPartsBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, factory).get(PartsViewModel::class.java)
        viewModel.apiListener = this

        imvClose?.setOnClickListener {
            dismissAllowingStateLoss()
        }

        binding.txtHeaderDialog.text = strItemName

        viewModel.getLoggedInUser().observe(this, Observer { user ->
            if (user != null) {
                viewModel.getItemAndPartDetail(intItemSlNo, strSearchBy)
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
                            .observe(this@UsedItemsDialog, Observer { user ->

                                if (user != null) {
                                    viewModel.getItemAndPartDetail(
                                            intItemSlNo,
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

                        viewModel.getItemAndPartDetail(
                                intItemSlNo,
                                strSearchBy
                        )
                }
            })
            false
        }

    }

    private fun List<UsedItemAndPartData?>.toItemAndPart(): List<UsedItemAndPartDialogRow?> {
        return this.map {
           UsedItemAndPartDialogRow(it, requireContext())
        }
    }

    private fun bindItemsUI(list: List<UsedItemAndPartData?>?) = Coroutines.main {

        list?.let {
            initItemRecyclerView(it.toItemAndPart())
        }
    }

    private fun initItemRecyclerView(toItemAndPartData: List<UsedItemAndPartDialogRow?>) {
        val mAdapter = GroupAdapter<GroupieViewHolder>().apply {
            addAll(toItemAndPartData)
        }

        binding.rvPartsDetail.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = mAdapter
        }
    }

    override fun onStarted(callFrom: String) {
        binding.viewCommon.showProgressBar()
    }

    override fun onSuccess(_object: List<Any?>, callFrom: String) {
        binding.viewCommon.hide()
        val data = _object as List<Any?>
        if (data.isNullOrEmpty()) {
            binding.viewCommon.showNoData()
        }else{
            bindItemsUI(data as List<UsedItemAndPartData?>)
        }

    }

    override fun onError(message: String, callFrom: String, isNetworkError: Boolean) {

        binding.viewCommon.hide()
        if (isNetworkError) {
            binding.viewCommon.showNoInternet()
        } else {
            binding.viewCommon.showServerError()
        }
        context?.toast(message)
    }

    override fun onValidationError(message: String, callFrom: String) {
        context?.toast(message)
    }

    companion object {
        fun newInstance() = UsedItemsDialog()
    }


}