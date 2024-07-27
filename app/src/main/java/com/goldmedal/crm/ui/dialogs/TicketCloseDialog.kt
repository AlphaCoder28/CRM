package com.goldmedal.hrapp.ui.dialogs


import android.app.Dialog
import android.content.DialogInterface
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
import com.goldmedal.crm.R
import com.goldmedal.crm.common.ApiStageListener
import com.goldmedal.crm.data.model.UpdateVisitStatusData
import com.goldmedal.crm.databinding.DialogCloseBinding
import com.goldmedal.crm.ui.ticket.TicketViewModel
import com.goldmedal.crm.ui.ticket.TicketViewModelFactory
import com.goldmedal.crm.util.toast
import kotlinx.android.synthetic.main.dialog_close.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class TicketCloseDialog : DialogFragment(), KodeinAware, ApiStageListener<Any> {

    override val kodein by kodein()

    private val factory: TicketViewModelFactory by instance()
    private lateinit var viewModel: TicketViewModel
    private var attBinding: DialogCloseBinding? = null

    private val binding get() = attBinding!!

    var actionId = -1
    var ticketID = -1
    var ticketNo = ""
    var strLatitude = ""
    var strLongitude = ""
    var strLocation = ""

    var callBack: OnCloseReceived? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        arguments?.let {

            actionId = arguments?.getInt("actionId") ?: -1
            ticketID = arguments?.getInt("ticketId") ?: -1
            ticketNo = arguments?.getString("ticketNo") ?: ""
            strLatitude = arguments?.getString("deviceLatitude") ?: "-"
            strLongitude = arguments?.getString("deviceLongitude") ?: "-"
            strLocation = arguments?.getString("deviceAddress") ?: "Unnamed road"
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
        attBinding = DialogCloseBinding.inflate(inflater, container, false)
//        attBinding =
//            DataBindingUtil.inflate(inflater, R.layout.dialog_Close, container, false)
        return attBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, factory).get(TicketViewModel::class.java)

        //attBinding?.viewmodel = viewModel
        viewModel.apiListener = this

        if (actionId == 5) {
            tvPopupHeader.text = "Re-Assign"
        }
        if (actionId == 6) {
            tvPopupHeader.text = "Close"
        }

        binding.imvClose?.setOnClickListener {
            dismissAllowingStateLoss()
        }

        binding.btnClose?.setOnClickListener {
            viewModel.actionId = actionId
            viewModel.getLoggedInUser().observe(viewLifecycleOwner, Observer { user ->
                if (user != null) {
                    viewModel.strUpdateStatusRemarks =
                            binding.layoutEngineerRemarkClosePopup.edtEngineerRemark.text.toString()
                    viewModel.updateTicketStatus(user.UserId, ticketID, strLatitude, strLongitude, strLocation, false, "-", "0.0",false,false)

                }
            })

        }

    }


    override fun onStarted(callFrom: String) {
        progress_bar?.start()
    }

    override fun onSuccess(_object: List<Any?>, callFrom: String) {
        progress_bar?.stop()

        if (callFrom == "update_ticket_status") {
            val updateStatusData = _object as List<UpdateVisitStatusData>
            showSuccessAlert(updateStatusData[0].statusMessage)
        }
    }


    private fun showSuccessAlert(apiMessage: String) {

        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        /*var statusMsg = ""
        if (actionId == 5) {
            statusMsg = "Re-Assign"
        } else if (actionId == 6) {
            statusMsg = "Close"
        }
        val message =
                "Ticket no $ticketNo status updated as $statusMsg successfully!"*/
        builder.setMessage(apiMessage)
        builder.setPositiveButton(R.string.str_ok, object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface, id12: Int) {
                callBack?.onCloseReceived()
                dismissAllowingStateLoss()

            }
        })
        val alertDialog = builder.create()
        alertDialog.show()

    }

    override fun onDestroy() {
        super.onDestroy()
        attBinding = null
    }


    override fun onError(message: String, callFrom: String, isNetworkError: Boolean) {
        progress_bar?.stop()
        context?.toast(message)
    }


    companion object {
        fun newInstance() = TicketCloseDialog()
        private const val REFRESH_RESULT_CODE = 102
    }

    interface OnCloseReceived {
        fun onCloseReceived()
    }


    override fun onValidationError(message: String, callFrom: String) {
        context?.toast(message)
    }


}