package com.goldmedal.crm.ui.dashboard.Manager


import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ManagerViewModelTest : TestCase(){


    private lateinit var viewModel: ManagerViewModel

    public override fun setUp() {
        super.setUp()

        val context = ApplicationProvider.getApplicationContext<Context>()



    }
}