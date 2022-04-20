package com.goldmedal.crm.ui.dashboard


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.goldmedal.crm.R



open class TransitionsActivity : AppCompatActivity() {
    override fun finish() {
        super.finish()
        overridePendingTransitionExit()
    }

    override fun startActivity(intent: Intent) {
        super.startActivity(intent)
        overridePendingTransitionEnter()
    }




    /**
     * Overrides the pending Activity transition by performing the "Enter" animation.
     */
    protected fun overridePendingTransitionEnter() {
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
    }

    /**
     * Overrides the pending Activity transition by performing the "Exit" animation.
     */
    protected fun overridePendingTransitionExit() {
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
    }

//    override fun onBackPressed() {
//        super.onBackPressed()
//        overridePendingTransitionExit()
//    }
}