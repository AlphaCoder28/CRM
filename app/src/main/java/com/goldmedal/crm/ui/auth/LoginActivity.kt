package com.goldmedal.crm.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.goldmedal.crm.R
import com.goldmedal.crm.data.model.SessionData
import com.goldmedal.crm.databinding.ActivityLoginBinding
import com.goldmedal.crm.ui.dashboard.DashboardActivity
import com.goldmedal.crm.util.getDeviceId
import com.goldmedal.crm.util.snackbar
import kotlinx.android.synthetic.main.activity_login.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

//<!--added by shetty 6 jan 21-->
class LoginActivity : AppCompatActivity(), AuthListener<Any>, KodeinAware {

    override val kodein by kodein()
    private val factory: LoginViewModelFactory by instance()

    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val binding: ActivityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)

         viewModel = ViewModelProvider(this, factory).get(LoginViewModel::class.java)
        binding.viewmodel = viewModel

        viewModel.authListener = this
        viewModel.strDeviceId = getDeviceId(this@LoginActivity)

       // viewModel.strGeneratedCaptcha = generateRandomCaptcha()
     //   tvCaptcha.text = viewModel.strGeneratedCaptcha

        viewModel.getLoggedInUser().observe(this, Observer { user ->

            if (user != null) {
                Intent(this, DashboardActivity::class.java).also {
                    it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(it)

                }
            }
        })

        //Get FireBase Registration  Token (pushwoosh id)

//        FirebaseInstanceId.getInstance().instanceId
//                .addOnCompleteListener(OnCompleteListener { task ->
//                    if (!task.isSuccessful) {
//                        Log.w("TAG", "getInstanceId failed", task.exception)
//                        return@OnCompleteListener
//                    }
//
//                    // Get new Instance ID token
//                    viewModel.strFCMToken = task.result?.token
//
//                    // Log and toast
//                    //    val msg = getString(R.string.msg_token_fmt, token)
//                    //   Log.d(TAG, msg)
//                    //Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
//                })



        verify_otp_layout?.setOnClickListener {

               VerifyOTPActivity.start(this)
        }

    }


    override fun onStarted() {
        progress_bar?.start()
//
//        Intent(this, DashboardActivity::class.java).also {
//            //  it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
//            it.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
//
//            startActivity(it)
//        }
    }

//    override fun onSuccess(_object: List<Any?>) {
//
//
//    }

    override fun onSuccess(_object: List<Any?>, callFrom: String) {
        progress_bar?.stop()

        if (callFrom.equals("authenticate_email")) {

            val sessionData = _object as List<SessionData>

            if(sessionData.isNotEmpty()){
                 viewModel.authenticatePassword(sessionData[0].LogNo,sessionData[0].SessionId)
            }


           // viewModel.strCaptcha = sessionData[0].SessionId

        }

        if (callFrom == "authenticate_password") {

            Intent(this, DashboardActivity::class.java).also {
                //  it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                it.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

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



//    override fun setCaptcha(strCaptcha: String) {
//        tvCaptcha.text = strCaptcha
//    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, LoginActivity::class.java))
        }
    }
}
