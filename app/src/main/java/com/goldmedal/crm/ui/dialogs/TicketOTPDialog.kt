package com.goldmedal.hrapp.ui.dialogs


import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.goldmedal.crm.R
import com.goldmedal.crm.common.ApiStageListener
import com.goldmedal.crm.data.network.GlobalConstant
import com.goldmedal.crm.databinding.DialogOtpBinding
import com.goldmedal.crm.ui.ticket.TicketViewModel
import com.goldmedal.crm.ui.ticket.TicketViewModelFactory
import com.goldmedal.crm.util.TimeDurationUtil
import com.goldmedal.crm.util.toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.dialog_otp.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class TicketOTPDialog : DialogFragment(), KodeinAware, ApiStageListener<Any> {

    override val kodein by kodein()

    private val factory: TicketViewModelFactory by instance()
    private lateinit var viewModel: TicketViewModel
    private lateinit var attBinding: DialogOtpBinding
    var strGoodOtp : String? = null
    var strBadOtp : String? = null
    var callBack: OnOTPReceived? = null

    private lateinit var countdown_timer: CountDownTimer
    private  var time_in_milli_seconds = 60000L

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog

    }

    override fun onResume() {
        super.onResume()

        val metrics = requireContext().resources.displayMetrics
        val screenWidth = (metrics.widthPixels * 0.85).toInt()
        dialog?.window?.setLayout(screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog?.window?.setGravity(Gravity.CENTER)
        dialog?.setCanceledOnTouchOutside(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        attBinding =
            DataBindingUtil.inflate(inflater, R.layout.dialog_otp, container, false)
        return attBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, factory).get(TicketViewModel::class.java)

        attBinding.viewmodel = viewModel
        viewModel.apiListener = this

        if (time_in_milli_seconds > 0) {
            startTimer(time_in_milli_seconds)
            tvResend.isClickable = false
        }

//        viewModel.getLoggedInUser().observe(viewLifecycleOwner, Observer { user ->
//            if (user != null) {
//                viewModel.userId = user.UserId
//            }
//        })

      // viewModel.strOtp = arguments?.getString("otp")
//        viewModel.strMobileNo = arguments?.getString("mobileNo")!!
//        viewModel.ticketId = arguments?.getInt("ticketId")!!
//        viewModel.strCustName = arguments?.getString("custName")!!


        imvClose?.setOnClickListener {
            dismissAllowingStateLoss()
        }


        tvResend?.setOnClickListener {
            callBack?.onResendOtp(1)
            time_in_milli_seconds = 60000L
            startTimer(time_in_milli_seconds)
        }

        btnSubmit?.setOnClickListener {
            if (input_otp.text.toString() == strGoodOtp) {
                showSuccessMessage("OTP Verified is Good",input_otp.text.toString())
            } else if (input_otp.text.toString() == strBadOtp) {
                showSuccessMessage("OTP Verified is Bad",input_otp.text.toString())
            }else {
                showSuccessMessage("Invalid OTP",input_otp.text.toString())
            }
        }

// - - -  In case is user doesnt want to submit otp - - - - - -
        btnCancel?.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())

                    .setMessage("Do you want to proceed without OTP??")

                    .setPositiveButton(resources.getString(R.string.str_yes)) { dialog, which ->
                        callBack?.onOtpReceived("0")
                        dismiss()
                    }

                    .setNegativeButton(resources.getString(R.string.str_no)) { dialog, which ->
                        dismiss()
                    }

                    .show()

        }

        //Pincode TextField text changed
//        input_otp?.doAfterTextChanged {
//
//
//            if (it?.length == 4) {
//
//            } else {
//                input_otp.setText("")
//            }
//
//        }

    }


    private fun startTimer(time_in_seconds: Long) {
        countdown_timer = object : CountDownTimer(time_in_seconds, GlobalConstant.TICK_TOCK_INTERVAL) {
            override fun onFinish() {
                Log.d("TAG", "onFinish: +timer timeout")
                if(tvResend != null) {
                    tvResend.text = "Resend OTP"
                    tvResend.isClickable = true
                }
            }

            override fun onTick(p0: Long) {
                time_in_milli_seconds = p0
                if(tvResend != null){
                    tvResend.text = TimeDurationUtil.formatMinutesSeconds(time_in_milli_seconds)
                    tvResend.isClickable = false
                }
            }
        }
        countdown_timer.start()
    }



    override fun onStarted(callFrom: String) {
        progress_bar?.start()
    }

    override fun onSuccess(_object: List<Any?>, callFrom: String) {
        progress_bar?.stop()
    }

    private fun showSuccessMessage(message: String,otp:String) {
        MaterialAlertDialogBuilder(requireContext())

            .setMessage(message)

            .setPositiveButton(resources.getString(R.string.str_ok)) { dialog, which ->
                if (!message.equals("Invalid OTP")) {
                    callBack?.onOtpReceived(otp)
                }

                dismiss()
            }

            .show()
    }


    override fun onError(message: String, callFrom: String, isNetworkError: Boolean) {
        progress_bar?.stop()
        context?.toast(message)
    }


    companion object {
        fun newInstance() = TicketOTPDialog()
    }

    interface OnOTPReceived {
        fun onOtpReceived(otp: String)
        fun onResendOtp(resend:Int)
    }


    override fun onValidationError(message: String, callFrom: String) {
        context?.toast(message)
    }


}