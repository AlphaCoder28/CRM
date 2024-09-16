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
import com.goldmedal.crm.util.snackbar
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance


class VerifyOTPActivity : AppCompatActivity(), AuthListener<Any>, KodeinAware {

    override val kodein by kodein()
    private lateinit var mBinding: OtpLayoutBinding
    private val factory: LoginViewModelFactory by instance()
    private lateinit var viewModel: LoginViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = DataBindingUtil.setContentView(this, R.layout.otp_layout)

        viewModel = ViewModelProvider(this, factory).get(LoginViewModel::class.java)
        mBinding.viewmodel = viewModel

        viewModel.authListener = this
        viewModel.strDeviceId = getDeviceId(this@VerifyOTPActivity)
    }

    override fun onStarted() {
        mBinding.progressBar.start()
    }

    override fun onSuccess(_object: List<Any?>, callFrom: String) {

        mBinding.progressBar.stop()

        if(callFrom == "get_otp"){
          mBinding.getOtpLayout.visibility = View.GONE
          mBinding.verifyOtpLayout.visibility = View.VISIBLE
        }

        if (callFrom == "verify_otp"){
             Intent(this, DashboardActivity::class.java).also {
                    it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(it)
                }
        }
    }

    override fun onFailure(message: String, callFrom: String, isNetworkError: Boolean) {
         mBinding.progressBar.stop()
         mBinding.rootLayout.snackbar(message)
    }

    override fun onValidationError(message: String) {
       mBinding.rootLayout.snackbar(message)
    }

     companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, VerifyOTPActivity::class.java))
        }
    }
}
