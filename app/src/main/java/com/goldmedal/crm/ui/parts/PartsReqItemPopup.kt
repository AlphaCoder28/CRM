package com.goldmedal.crm.ui.parts

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.goldmedal.crm.common.ApiStageListener
import com.goldmedal.crm.data.model.SelectPartsListData
import com.goldmedal.crm.databinding.DialogPartsRequirementItemBinding
import com.goldmedal.crm.util.Coroutines
import com.goldmedal.crm.util.toast
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class PartsReqItemPopup : DialogFragment(), KodeinAware, ApiStageListener<Any> {

    override val kodein by kodein()

    private val factory: PartsViewModelFactory by instance()
    private lateinit var viewModel: PartsViewModel

    private lateinit var binding: DialogPartsRequirementItemBinding

    var itemReqNo = String()
    var callFrom = String()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        arguments?.let {
            itemReqNo = arguments?.getString("RequestNo") ?: "0"
            callFrom = arguments?.getString("CallFrom") ?: ""
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
    ): View {

        binding = DialogPartsRequirementItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, factory).get(PartsViewModel::class.java)
        viewModel.apiListener = this

        binding.imvClose.setOnClickListener {
            dismissAllowingStateLoss()
        }

        viewModel.getLoggedInUser().observe(this, Observer { user ->
            if (user != null) {
                if(callFrom == "parts"){
                    binding.txtQtyHeader.text = "Quantity"
                    viewModel.getRequestedItemListByUser(itemReqNo)
                }

                if(callFrom == "status"){
                    binding.txtQtyHeader.text = "Available Quantity"
                    viewModel.getStockPartsList(user.UserId ?: 0, itemReqNo,"")
                }

            }
        })
    }

    private fun List<SelectPartsListData?>.toParts(): List<PartsRequirementItemDialogRow?> {
        return this.map {
            PartsRequirementItemDialogRow(it, requireContext())
        }
    }

    private fun bindItemsUI(list: List<SelectPartsListData?>?) = Coroutines.main {

        list?.let {
            initRecyclerView(it.toParts())

            val totalQty = list.map { ((it?.ActualQuantity ?: "0").toDouble().toInt()) }.sum()
            binding.txtTotalQty.text = totalQty.toString()
        }
    }

    private fun initRecyclerView(toParts: List<PartsRequirementItemDialogRow?>) {
        val mAdapter = GroupAdapter<GroupieViewHolder>().apply {
            addAll(toParts)
        }

        binding.rvPartsReqItemsDetail.apply {
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
        if (data.isEmpty()) {
            binding.llPartsReqItemMain.visibility = View.INVISIBLE
            binding.viewCommon.showNoData()
        }else{
            binding.llPartsReqItemMain.visibility = View.VISIBLE
            bindItemsUI(data as List<SelectPartsListData?>)
        }

    }

    override fun onError(message: String, callFrom: String, isNetworkError: Boolean) {
        binding.llPartsReqItemMain.visibility = View.INVISIBLE
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
        fun newInstance() = PartsReqItemPopup()
    }


}