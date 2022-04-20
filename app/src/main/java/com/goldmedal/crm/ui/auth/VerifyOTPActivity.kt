package com.goldmedal.crm.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View

import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.goldmedal.crm.R
import com.goldmedal.crm.databinding.OtpLayoutBinding
import com.goldmedal.crm.ui.dashboard.DashboardActivity

import com.goldmedal.crm.util.getDeviceId
import com.goldmedal.crm.util.hide
import com.goldmedal.crm.util.show
import com.goldmedal.crm.util.snackbar

import kotlinx.android.synthetic.main.otp_layout.*
import kotlinx.android.synthetic.main.otp_layout.progress_bar

import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance


class VerifyOTPActivity : AppCompatActivity(), AuthListener<Any>, KodeinAware {

    override val kodein by kodein()
    private val factory: LoginViewModelFactory by instance()
    private lateinit var viewModel: LoginViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val binding: OtpLayoutBinding = DataBindingUtil.setContentView(this, R.layout.otp_layout)

        viewModel = ViewModelProvider(this, factory).get(LoginViewModel::class.java)
        binding.viewmodel = viewModel

        viewModel.authListener = this
        viewModel.strDeviceId = getDeviceId(this@VerifyOTPActivity)
    }

    override fun onStarted() {
        progress_bar?.start()
    }

    override fun onSuccess(_object: List<Any?>, callFrom: String) {

        progress_bar?.stop()

        if(callFrom == "get_otp"){
          get_otp_layout?.visibility = View.GONE
          verify_otp_layout?.visibility = View.VISIBLE
        }

        if (callFrom == "verify_otp"){
             Intent(this, DashboardActivity::class.java).also {
                    it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(it)
                }
        }
    }

    override fun onFailure(message: String, callFrom: String, isNetworkError: Boolean) {
         progress_bar?.stop()
         root_layout?.snackbar(message)
    }

    override fun onValidationError(message: String) {
       root_layout?.snackbar(message)
    }

     companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, VerifyOTPActivity::class.java))
        }
    }
}
