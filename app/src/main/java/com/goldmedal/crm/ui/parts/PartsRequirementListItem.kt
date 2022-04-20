package com.goldmedal.crm.ui.parts

import android.content.Context
import android.view.View
import com.goldmedal.crm.R
import com.goldmedal.crm.data.model.GetRequestPartListData
import com.goldmedal.crm.databinding.PartsRequirementItemRowBinding
import com.goldmedal.crm.util.interfaces.PartRequirementClickListener
import com.goldmedal.crm.util.interfaces.UsedPartListener
import com.xwray.groupie.viewbinding.BindableItem

class PartsRequirementListItem(
    private val getRequestPartListData:  GetRequestPartListData?,
    private val context: Context,
    private val callBackListener: PartRequirementClickListener
) : BindableItem<PartsRequirementItemRowBinding>() {

    override fun bind(viewBinding: PartsRequirementItemRowBinding, position: Int) {

        viewBinding.apply {

            txtCustName.text = getRequestPartListData?.CustName
            txtContactNumber.text = getRequestPartListData?.ContactNo
            txtRequestDate.text = getRequestPartListData?.RequestDate
            txtSlNo.text = getRequestPartListData?.RequestNo.toString()
            txtTicketNo.text = getRequestPartListData?.TktNo
            txtTotalQty.text = getRequestPartListData?.TotalQuantity.toString()
           // txtStatus.text = "View"
            txtTotalParts.text = getRequestPartListData?.TotalPart.toString()
            txtTktStatus.text = getRequestPartListData?.TktStatus.toString()

            // - - - - - Passing only request No API
            txtTotalParts.setOnClickListener {
                callBackListener?.itemClicked(getRequestPartListData?.RequestNo,"parts")
            }

            // - - - Passing userID and Request No API
            txtStatus.setOnClickListener {
                callBackListener?.itemClicked(getRequestPartListData?.RequestNo,"status")
            }
        }
    }

    override fun getLayout() = R.layout.parts_requirement_item_row


    override fun initializeViewBinding(view: View) = PartsRequirementItemRowBinding.bind(view)


}