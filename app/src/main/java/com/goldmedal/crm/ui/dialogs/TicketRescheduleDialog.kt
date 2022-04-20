package com.goldmedal.hrapp.ui.dialogs


import android.app.DatePickerDialog
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.SpannableString
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.goldmedal.crm.R
import com.goldmedal.crm.common.ApiStageListener
import com.goldmedal.crm.data.model.GetTimeSlots
import com.goldmedal.crm.databinding.DialogRescheduleBinding
import com.goldmedal.crm.ui.ticket.TicketViewModel
import com.goldmedal.crm.ui.ticket.TicketViewModelFactory
import com.goldmedal.crm.util.toast
import kotlinx.android.synthetic.main.dialog_reschedule.*
import org.angmarch.views.OnSpinnerItemSelectedListener
import org.angmarch.views.SpinnerTextFormatter
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.util.*
import androidx.lifecycle.Observer


class TicketRescheduleDialog : DialogFragment(), KodeinAware, ApiStageListener<Any> {

    override val kodein by kodein()

    private val factory: TicketViewModelFactory by instance()
    private lateinit var viewModel: TicketViewModel
    private var attBinding: DialogRescheduleBinding? = null
   // private lateinit var attBinding: DialogRescheduleBinding
    private val binding get() = attBinding!!

    var ticketID = -1
    var ticketNo = ""
    var strLatitude = ""
    var strLongitude = ""
    var strLocation = ""

    var callBack: OnRescheduleReceived? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        arguments?.let {

            ticketID = arguments?.getInt("ticketId") ?: -1
            ticketNo = arguments?.getString("ticketNo") ?:""
            strLatitude = arguments?.getString("deviceLatitude") ?: "-"
            strLongitude = arguments?.getString("deviceLongitude")?: "-"
            strLocation = arguments?.getString("deviceAddress")?: "Unnamed road"
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
        attBinding = DialogRescheduleBinding.inflate(inflater, container, false)
//        attBinding =
//            DataBindingUtil.inflate(inflater, R.layout.dialog_reschedule, container, false)
        return attBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, factory).get(TicketViewModel::class.java)

        //attBinding?.viewmodel = viewModel
        viewModel.apiListener = this

        viewModel.getTimeSlots()

        binding.imvClose?.setOnClickListener {
            dismissAllowingStateLoss()
        }

        binding.btnReschedule?.setOnClickListener {
            viewModel.actionId = 4
            viewModel.getLoggedInUser().observe(viewLifecycleOwner, Observer { user ->
                if (user != null) {
                    viewModel.strUpdateStatusRemarks =
                            binding.layoutEngineerRemarkPopup.edtEngineerRemark.text.toString()
                    viewModel.updateTicketStatus(user.UserId, ticketID, strLatitude, strLongitude, strLocation, false, "-", "0.0",false,false)

                }
            })

        }

        binding.layoutReschedulePopup.txtAppointmentDate.setOnClickListener {


            //start after 1 day from now

            val startDate = Calendar.getInstance()
            startDate.add(Calendar.DAY_OF_MONTH, 1)

            val mYear = startDate[Calendar.YEAR]
            val mMonth = startDate[Calendar.MONTH]
            val mDay = startDate[Calendar.DAY_OF_MONTH]


            //end after 1 week from now
            val endDate = Calendar.getInstance()
            endDate.add(Calendar.DAY_OF_MONTH, 7)

            val endDatePicker = DatePickerDialog(
                    requireContext(),
                    { view, year, monthOfYear, dayOfMonth ->
                        binding.layoutReschedulePopup.txtAppointmentDate.text = String.format(
                                Locale.getDefault(),
                                "%d/%d/%d",
                                dayOfMonth,
                                monthOfYear + 1,
                                year
                        )
                        viewModel.strRescheduleDate =
                                (monthOfYear + 1).toString() + "-" + dayOfMonth + "-" + year
                    }, mYear, mMonth, mDay
            )
            endDatePicker.datePicker.minDate = startDate.timeInMillis
            endDatePicker.datePicker.maxDate = endDate.timeInMillis
            endDatePicker.show()
        }


    }


    private fun bindTimeSlots(list: MutableList<GetTimeSlots?>?) {

        list?.let {
            val textFormatter =
                    SpinnerTextFormatter<GetTimeSlots> { obj ->
                        SpannableString(
                                obj.TimeSlot
                        )
                    }
            binding.layoutReschedulePopup.spinnerTimeSlot.setSpinnerTextFormatter(textFormatter)
            binding.layoutReschedulePopup.spinnerTimeSlot.setSelectedTextFormatter(textFormatter)

            binding.layoutReschedulePopup.spinnerTimeSlot.onSpinnerItemSelectedListener =
                    OnSpinnerItemSelectedListener { parent, view, position, id ->
                        val item = binding.layoutReschedulePopup.spinnerTimeSlot.selectedItem as GetTimeSlots
                        viewModel.timeSlotId = item.TimeSlotID
                    }

            binding.layoutReschedulePopup.spinnerTimeSlot.attachDataSource(list)
        }


    }



    override fun onStarted(callFrom: String) {
        progress_bar?.start()
    }

    override fun onSuccess(_object: List<Any?>, callFrom: String) {
        progress_bar?.stop()

        if (callFrom == "time_slots") {
            val timeSlots = _object as MutableList<GetTimeSlots?>?
            timeSlots?.add(0, GetTimeSlots("Select", -1))
            bindTimeSlots(timeSlots)
        }
        if (callFrom == "update_ticket_status") {
            showSuccessAlert()
        }
    }


    private fun showSuccessAlert() {

        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())

        val message =
                "Ticket no $ticketNo status updated as Re-Schedule successfully!"
        builder.setMessage(message)
        builder.setPositiveButton(R.string.str_ok, object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface, id12: Int) {
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
        fun newInstance() = TicketRescheduleDialog()
        private const val REFRESH_RESULT_CODE = 102
    }

    interface OnRescheduleReceived {
        fun onRescheduleReceived()
    }


    override fun onValidationError(message: String, callFrom: String) {
        context?.toast(message)
    }


}