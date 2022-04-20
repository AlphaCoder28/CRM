package com.goldmedal.crm.ui.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.goldmedal.crm.R
import com.goldmedal.crm.common.transform.PageTransformerFactory
import com.goldmedal.crm.common.transform.TransformerStyle
import com.goldmedal.crm.data.adapters.IntroAdapter
import com.goldmedal.crm.data.model.CustomBean
import com.goldmedal.crm.data.model.viewholder.CustomPageViewHolder
import com.zhpan.bannerview.BannerViewPager
import com.zhpan.indicator.enums.IndicatorSlideMode
import kotlinx.android.synthetic.main.activity_intro.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.util.*


class IntroActivity : AppCompatActivity(), KodeinAware {
    //    private var decentBanner: DecentBanner? = null
//    private var Views: MutableList<View>? = null
//    private var Titles: MutableList<String>? = null

    override val kodein by kodein()
    private val factory: LoginViewModelFactory by instance()
    private lateinit var mViewPager: BannerViewPager<CustomBean, CustomPageViewHolder>

    private val des = arrayOf("Goldmedal is synonymous with\nworld-class electrical brands", "Switch to the amazing\nto come across products of tomorrow", "We've been innovating since\ninception in 1979 \nIt's a legacy we are proud of and \nwhich will never change")
    private val introJson = arrayOf(R.raw.splash_1, R.raw.splash_2, R.raw.splash_3)
    private val transforms = intArrayOf( TransformerStyle.ACCORDION, TransformerStyle.DEPTH, TransformerStyle.ROTATE, TransformerStyle.SCALE_IN)
    private val backgroundRes = intArrayOf( R.color.colorMaterialIndigo, R.color.colorMaterialBlue, R.color.colorMaterialPink)
    private val data: List<CustomBean>
        get() {
            val list = ArrayList<CustomBean>()
//            for (i in mDrawableList.indices) {
            for (i in introJson.indices) {
                val customBean = CustomBean()
                customBean.imageRes = introJson[i]
                customBean.backgroundRes = backgroundRes[i]
                customBean.imageDescription = des[i]
                list.add(customBean)
            }
            return list
        }

    //private val myImageList = intArrayOf(R.layout.intro_screen_content_1, R.layout.intro_screen_content_2, R.layout.intro_screen_content_3, R.layout.intro_screen_content_4)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        val viewModel = ViewModelProvider(this, factory).get(LoginViewModel::class.java)

        setupViewPager()
        updateUI(0)


        btn_start?.setOnClickListener {



            viewModel.introInit()

            LoginActivity.start(this)
            finish()
        }


    }

//    private fun init() {
//
//        btnSkip = findViewById(R.id.btnSkip) as Button
//
//        btnSkip!!.setOnClickListener(object : View.OnClickListener {
//            override fun onClick(v: View?) {
//
//                Intent(this@IntroActivity, DashboardActivity::class.java).also {
//                    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                    startActivity(it)
//                }
//            }
//        })
//
//
//
////        for (i in myImageList.indices) {
////            val view = layoutInflater.inflate(myImageList[i], null)
////            Views!!.add(view)
////            Titles!!.add("●")
////        }
//
//
//
//    //    decentBanner!!.setGradientEnabled(false)
//    //    decentBanner!!.setPageIndicatorEnabled(false)
//     //   decentBanner!!.start(Views, Titles, 3, 600)
//
//
//    }

    private fun setupViewPager() {
        mViewPager = findViewById(R.id.viewpager)
        mViewPager.apply {
            setCanLoop(false)
            setPageTransformer(PageTransformerFactory.createPageTransformer(transforms[Random().nextInt(4)]))
            setIndicatorMargin(0, 0, 0, resources.getDimension(R.dimen.dp_70).toInt())
            setIndicatorSliderGap(resources.getDimension(R.dimen.dp_10).toInt())
            setIndicatorSlideMode(IndicatorSlideMode.SMOOTH)
            setIndicatorSliderRadius(resources.getDimension(R.dimen.dp_4).toInt(), resources.getDimension(R.dimen.dp_6).toInt())
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    //BannerUtils.log("position:$position")
                    updateUI(position)
                }
            })
            adapter = IntroAdapter(this@IntroActivity).apply {
//                mOnSubViewClickListener = CustomPageViewHolder.OnSubViewClickListener { _, position ->

//                    ToastUtils.show("Logo Clicked,position:$position") }
            }
            setIndicatorSliderColor(ContextCompat.getColor(this@IntroActivity, R.color.white),
                    ContextCompat.getColor(this@IntroActivity, R.color.white_alpha_75))
        }.create(data)
    }

    private fun updateUI(position: Int) {
        tv_describe?.text = des[position]
        val translationAnim = ObjectAnimator.ofFloat(tv_describe, "translationX", -120f, 0f)
        translationAnim.apply {
            duration = ANIMATION_DURATION.toLong()
            interpolator = DecelerateInterpolator()
        }
        val alphaAnimator = ObjectAnimator.ofFloat(tv_describe, "alpha", 0f, 1f)
        alphaAnimator.apply {
            duration = ANIMATION_DURATION.toLong()
        }
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(translationAnim, alphaAnimator)
        animatorSet.start()

        if (position == mViewPager.data.size - 1 && btn_start?.visibility == View.GONE) {
            btn_start?.visibility = View.VISIBLE
            ObjectAnimator
                    .ofFloat(btn_start, "alpha", 0f, 1f)
                    .setDuration(ANIMATION_DURATION.toLong()).start()
        } else {
            btn_start?.visibility = View.GONE
        }
    }


    companion object {
        // private var btnSkip: Button? = null

        private const val ANIMATION_DURATION = 1300

            fun start(context: Context) {
                context.startActivity(Intent(context, IntroActivity::class.java))
            }

    }


}
