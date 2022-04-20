package com.goldmedal.crm.ui.ticket

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.goldmedal.crm.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet_options.*

class OptionsBottomSheetFragment : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_options, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
    }

    private fun setUpViews() {
        // We can have cross button on the top right corner for providing elemnet to dismiss the bottom sheet
        //iv_close.setOnClickListener { dismissAllowingStateLoss() }
        txt_scan.setOnClickListener {
            dismissAllowingStateLoss()
            mListener?.onItemClick("scan")

        }

        txt_search.setOnClickListener {
            dismissAllowingStateLoss()
            mListener?.onItemClick("search")
        }

        txt_eanNo.setOnClickListener {
            dismissAllowingStateLoss()
            mListener?.onItemClick("eanNo")
        }

        txt_productCode.setOnClickListener {
            dismissAllowingStateLoss()
            mListener?.onItemClick("productCode")
        }

    }

    private var mListener: ItemClickListener? = null




    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = parentFragment as ItemClickListener?
//        if (context is ItemClickListener) {
//            mListener = context as ItemClickListener
//        } else {
//            throw RuntimeException(
//                context.toString() + " must implement ItemClickListener"
//            )
//        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }
    interface ItemClickListener {
        fun onItemClick(param: String)
    }

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle): OptionsBottomSheetFragment {
            val fragment = OptionsBottomSheetFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}