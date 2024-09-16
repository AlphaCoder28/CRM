package com.goldmedal.crm.ui.dialogs


import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.goldmedal.crm.R
import com.goldmedal.crm.common.ApiStageListener
import com.goldmedal.crm.data.model.AcceptRejectTicket
import com.goldmedal.crm.data.model.PincodeWiseStateDistrictData
import com.goldmedal.crm.databinding.DialogTicketUnacceptanceBinding
import com.goldmedal.crm.util.Coroutines
import com.goldmedal.crm.util.getDeviceId
import com.goldmedal.crm.util.toast
import com.goldmedal.hrapp.ui.dialogs.TicketUnacceptanceViewModel
import com.goldmedal.hrapp.ui.dialogs.TicketUnacceptanceViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class TicketUnacceptanceDialog : DialogFragment(), KodeinAware, ApiStageListener<Any> {

    override val kodein by kodein()

    private val factory: TicketUnacceptanceViewModelFactory by instance()
    private lateinit var viewModel: TicketUnacceptanceViewModel

    private lateinit var attBinding: DialogTicketUnacceptanceBinding


    var callBack: OnRejectTicket? = null


    // private val GALLERY = 1
    //private val CAMERA = 2


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
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

        attBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_ticket_unacceptance, container, false)

        //set to adjust screen height automatically, when soft keyboard appears on screen

        return attBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, factory).get(TicketUnacceptanceViewModel::class.java)

        attBinding.viewmodel = viewModel
        viewModel.apiListener = this

        //     viewModel.imageSelectionListener = this

        viewModel.strDeviceId = context?.let { getDeviceId(context = it) }


        viewModel.getLoggedInUser().observe(viewLifecycleOwner, Observer { user ->
            if (user != null) {
                viewModel.userId = user.UserId

            }
        })

        viewModel.ticketNo = arguments?.getInt("ticketNo")
        viewModel.type = arguments?.getInt("type")!!
        viewModel.strLatitude = arguments?.getString("deviceLatitude")!!
        viewModel.strLongitude = arguments?.getString("deviceLongitude")!!
        viewModel.strLocation = arguments?.getString("deviceAddress")!!

        val items = listOf("Please Select", "Incorrectly Assigned", "Incorrect Address", "Others")
        val adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, items)
        (attBinding.spinnerReason.editText as? AutoCompleteTextView)?.setAdapter(adapter)


        (attBinding.spinnerReason.editText as? AutoCompleteTextView)?.setText(
                resources.getString(R.string.str_please_select),
                false
        )

        attBinding.imvClose.setOnClickListener {
            dismissAllowingStateLoss()
        }

        (attBinding.spinnerReason.editText as? AutoCompleteTextView)?.setOnItemClickListener { adapterView, _, i, l ->

            when (adapterView.getItemAtPosition(i)) {
                "Please Select" -> {
                    viewModel.reasonId = -1
                    attBinding.layoutIncorrectAddress.visibility = View.GONE
                }
                "Incorrectly Assigned" -> {
                    viewModel.reasonId = 1
                    attBinding.layoutIncorrectAddress.visibility = View.GONE
                }
                "Incorrect Address" -> {
                    viewModel.reasonId = 2
                    attBinding.layoutIncorrectAddress.visibility = View.VISIBLE

                }
                "Others" -> {
                    viewModel.reasonId = 3
                    attBinding.layoutIncorrectAddress.visibility = View.GONE
                }
            }


        }

//Pincode TextField text changed
        attBinding.edtPincode.doAfterTextChanged {

            if (it?.length == 6) {
                viewModel.getPincodeWiseStateDistrict()
            } else {
                resetTextFields()
            }

        }

    }

    private fun bindUI(list: List<PincodeWiseStateDistrictData?>?) = Coroutines.main {
        list?.let {
            viewModel.districtId = list[0]?.DistrictID
            viewModel.stateId = list[0]?.StateID

            attBinding.edtState.setText(list[0]?.StateName)
            attBinding.edtDistrict.setText(list[0]?.DistrictName)
        }
    }

    private fun resetTextFields() {
        viewModel.districtId = 0
        viewModel.stateId = 0

        attBinding.edtDistrict.setText("")
        attBinding.edtState.setText("")

    }

    override fun onStarted(callFrom: String) {
        attBinding.progressBar.start()
    }

    override fun onSuccess(_object: List<Any?>, callFrom: String) {
        attBinding.progressBar.stop()


        if (callFrom == "info_pincode") {
            bindUI(_object as List<PincodeWiseStateDistrictData?>)
        }

        if (callFrom == "tkt_unacceptance_decline") {
            val data = _object as List<AcceptRejectTicket?>
            showSuccessMessage(data[0]?.StatusMessage ?: "Ticket Rejected Successfully!")
        }

    }

    private fun showSuccessMessage(message: String) {
        MaterialAlertDialogBuilder(requireContext())

                .setMessage(message)

                .setNeutralButton(resources.getString(R.string.str_ok)) { dialog, which ->

                    callBack?.onRejectTkt()
                    dismiss()
                }

                .show()
    }


    override fun onError(message: String, callFrom: String, isNetworkError: Boolean) {
        attBinding.progressBar.stop()
        context?.toast(message)

        if (callFrom == "info_pincode") {
            resetTextFields()
        }
    }


    companion object {
        fun newInstance() = TicketUnacceptanceDialog()
    }

    interface OnRejectTicket {
        fun onRejectTkt()
    }


    override fun onValidationError(message: String, callFrom: String) {
        context?.toast(message)
    }


}