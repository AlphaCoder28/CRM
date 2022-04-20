package com.goldmedal.crm.ui.ticket

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.goldmedal.crm.R
import com.goldmedal.crm.common.ZoomOutPageTransformer
import com.goldmedal.crm.data.model.GetTicketDetailsData
import com.goldmedal.crm.util.interfaces.OnRefreshListener
import com.goldmedal.crm.util.toast
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

private const val NUM_PAGES = 2
private const val ARG_PARAM = "model_item"
private const val TAG = "TicketInfoActivity"
//<!--added by akshay-->
class TicketInfoActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private var modelItem: GetTicketDetailsData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ticket_info)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tabs);


        intent?.let {
            modelItem = it.getParcelableExtra(ARG_PARAM)
            if (modelItem != null) {
                Log.d(TAG, "onCreate: " + modelItem?.CustName)

                supportActionBar?.title = "#${modelItem?.TicketNo}"
            }
        }


        viewPager.setPageTransformer(ZoomOutPageTransformer())

        val pagerAdapter = ScreenSlidePagerAdapter(this)
        viewPager.adapter = pagerAdapter

        TabLayoutMediator(tabLayout, viewPager,
                object : TabLayoutMediator.TabConfigurationStrategy {
                    override fun onConfigureTab(tab: TabLayout.Tab, position: Int) {
                        tab.text = when (position) {
                            0 -> "Complain"
                            else -> "Activity"
                        }
                    }
                }).attach()
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }



    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = NUM_PAGES

        override fun createFragment(position: Int): Fragment {

            return when (position) {
                0 -> {
                    ComplainTabFragment.newInstance(modelItem)
                }
                else -> {
                    ActivityTabFragment.newInstance(modelItem?.TicketID ?: -1,modelItem?.TicketStatus)
                }
            }


        }
    }

    companion object {
        fun start(
            context: Context,
            modelItem: GetTicketDetailsData?
        ) {
            context.startActivity(
                Intent(context, TicketInfoActivity::class.java)
                .putExtra(ARG_PARAM, modelItem)
            )
        }
    }


}