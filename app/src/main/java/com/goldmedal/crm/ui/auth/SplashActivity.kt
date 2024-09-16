package com.goldmedal.crm.ui.auth

import android.animation.Animator
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageInfo
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.pm.PackageInfoCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.goldmedal.crm.R
import com.goldmedal.crm.data.network.responses.AppUpdateData
import com.goldmedal.crm.databinding.ActivitySplashBinding
import com.goldmedal.crm.ui.dashboard.DashboardActivity
import com.goldmedal.crm.util.toast
import com.goldmedal.hrapp.ui.auth.UpdateAppDialogFragment
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class SplashActivity : AppCompatActivity(), KodeinAware,AuthListener<Any>, UpdateAppDialogFragment.OnCancelUpdate {


    override val kodein by kodein()
    private lateinit var mBinding: ActivitySplashBinding
    private val factory: LoginViewModelFactory by instance()
    private lateinit var viewModel: LoginViewModel

    private val splashJson = arrayOf(R.raw.splash_1, R.raw.splash_2, R.raw.splash_3)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        viewModel = ViewModelProvider(this, factory).get(LoginViewModel::class.java)
        viewModel.authListener = this

        mBinding.lottie.setAnimation(splashJson.random())
        mBinding.lottie.playAnimation()

        mBinding.lottie.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator) {

            }

            override fun onAnimationEnd(p0: Animator) {
                checkPlayStoreVersion()
            }

            override fun onAnimationCancel(p0: Animator) {

            }

            override fun onAnimationRepeat(p0: Animator) {

            }

        })
    }

    private fun getVersionCode(): Int {
        val pInfo: PackageInfo = packageManager.getPackageInfo(packageName, 0)
        val longVersionCode = PackageInfoCompat.getLongVersionCode(pInfo)
        return longVersionCode.toInt()

    }

    private fun checkPlayStoreVersion() {
        val currentVersion: Int = getVersionCode()
//playStoreVersionCode here is version code from initial API
//forceUpdate here is boolean here from initial API
        var forceUpdate = true
        var playStoreVersionCode = 1

       // toast("VERSION  --  - - - -"+currentVersion.toString()+" --  - --  -- "+playStoreVersionCode.toString())

        if (playStoreVersionCode > currentVersion) {
            val updateAppDialog = UpdateAppDialogFragment.newInstance(forceUpdate, this)
            updateAppDialog.show(supportFragmentManager, UpdateAppDialogFragment.TAG)
        } else {
            continueInsideApp()
        }
    }

    fun continueInsideApp() {
        val isIntroScreenShown = viewModel.isIntroInit()

        if (isIntroScreenShown) {
            viewModel.getLoggedInUser().observe(this@SplashActivity, Observer { user ->
                if (user != null) {

                    Intent(this@SplashActivity, DashboardActivity::class.java).also {
                        it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(it)

                    }
                } else {
                    LoginActivity.start(this@SplashActivity)
                    finish()

                }
            })
        } else {
            IntroActivity.start(this@SplashActivity)
            finish()
        }
    }

    override fun pressedNoThanks() {
        continueInsideApp()
    }

    override fun pressedUpdate() {
        val appPackageName = packageName
        //val appUrl = "https://we.tl/t-s0PDUPuKpb"
        try {
            //viewModel.appUpdate(appUrl)
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
        } catch (anfe: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
        }
    }

    override fun onStarted() {

    }

    override fun onSuccess(_object: List<Any?>, callFrom: String) {
        if (callFrom.equals("app_update")) {

            val updateData = _object as List<AppUpdateData>

            if(updateData.isNotEmpty()){
                val url =  updateData[0].appUrl

                if(url.isEmpty()){
                    Toast.makeText(this, "You can view generated Invoice from Invoice history", Toast.LENGTH_SHORT).show()
                }else{
                    val browserIntent =
                        Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(browserIntent)
                }

            }
        }
    }

    override fun onFailure(message: String, callFrom: String, isNetworkError: Boolean) {
        toast(message)
    }

    override fun onValidationError(message: String) {
        toast(message)
    }
}
