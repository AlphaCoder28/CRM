package com.goldmedal.crm.ui.ticket

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.goldmedal.crm.R
import com.goldmedal.crm.common.ApiStageListener
import com.goldmedal.crm.common.FullscreenImageActivity
import com.goldmedal.crm.data.model.GetTicketDetailsData
import com.goldmedal.crm.databinding.ActivityTicketViewDetailsBinding
import com.goldmedal.crm.util.Coroutines
import com.goldmedal.crm.util.downloadFile
import kotlinx.android.synthetic.main.activity_ticket_view_details.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class TicketViewDetailsActivity : AppCompatActivity(), KodeinAware, ApiStageListener<Any> {
    override val kodein by kodein()

    private val factory: TicketViewModelFactory by instance()
    private lateinit var binding: ActivityTicketViewDetailsBinding
    private lateinit var viewModel: TicketViewModel
    private var ticketId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ticket_view_details)
        binding = ActivityTicketViewDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        viewModel = ViewModelProvider(this, factory).get(TicketViewModel::class.java)
        viewModel.apiListener = this

        intent?.let {
            ticketId = it.getIntExtra(ARG_TICKET_ID, -1)

            supportActionBar?.title = "#${it.getStringExtra(ARG_TICKET_NO)}"

        }

        viewModel.getLoggedInUser().observe(this, Observer { user ->

            if (user != null) {
                viewModel.getTicketDetails(user.UserId, ticketId ?: -1)
            }
        })

    }


    private fun bindUI(modelItem: GetTicketDetailsData?) = Coroutines.main {
        modelItem?.let {

            txt_tktNo.text = modelItem.TicketNo
            txt_div_name.text = modelItem.DivisionName
            txt_priority.text = modelItem.TicketPriority
            txt_date.text = modelItem.TicketDate
            txt_cust_name.text = modelItem.CustName
            txt_reschedule_date.text = modelItem.ReScheduleDate + "  " + modelItem.ReScheduledByName
            txt_reschedule_remark.text = modelItem.ReScheduleRemark
            txt_contact_no.text = modelItem.CustContactNo
            txt_cust_address.text = modelItem.CustAddress + ", " + modelItem.City + ", " + modelItem.Distrctnm + ", " + modelItem.statenm + ", " + modelItem.Pincode
            txt_cust_emailId.text = modelItem.EmailID
            txt_item_name.text = modelItem.ProductName
            txt_manufacture_date.text = modelItem.ManufactureDate
            txt_time_slot.text = modelItem.TimeSlot
            txt_prod_warranty.text = modelItem.WarrantyUptoDate
            txt_item_qr_code.text = modelItem.ItemQRCode
            txt_item_ean_no.text = modelItem.ItemEANNo
            txt_item_product_code.text = ""
            txt_item_purchase_date.text = modelItem.PurchaseDt
            txt_item_issues.text = modelItem.ProductIssues
            txt_item_descr.text = modelItem.ProductIssueDesc
            txt_party_name.text = modelItem.PartyName + "  " + modelItem.PartyTypeName
          //  txt_remarks.text = modelItem?.AssignRemark
            txt_party_address.text = modelItem.PartyAddress
            txtSymptoms.text = modelItem.Symptoms.ifEmpty { "-" }
            txtDefectReason.text = modelItem.DefectReason.ifEmpty { "-" }
            txtRepairActionType.text = modelItem.RepairActionType.ifEmpty { "-" }
            txtRepairType.text = modelItem.RepairType.ifEmpty { "-" }
            txtReplacementReason.text = modelItem.ReplacementReason.ifEmpty { "-" }

            if(modelItem.IsCheckedIn == 1){
                txt_checkedIn.text = "YES"
            }else{
                txt_checkedIn.text = "NO"
            }

            if(modelItem.IsDealerCall){
                txt_dealer_call.text = "YES"
            }else{
                txt_dealer_call.text = "NO"
            }

            if(!modelItem.IsNoRepair){
                txt_dealer_isNoRepair.text = "YES"
            }else{
                txt_dealer_isNoRepair.text = "NO"
            }

            if(!modelItem.IsInvoiceGenrated){
                txt_dealer_isNoRepair.text = "-"
            }

            if(modelItem.TicketStatus?.toLowerCase().equals("closed")){
                llImgMain.visibility = View.VISIBLE
            }else{
                llImgMain.visibility = View.GONE
            }


            val tktStatus= modelItem.TicketStatus

            when (tktStatus?.toLowerCase()) {

                "pending ticket" -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        txt_tktStatus.setTextColor(this.resources.getColor(R.color.colorYellow,null))
                    }
                }
                "visited" -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        txt_tktStatus.setTextColor(this.resources.getColor(R.color.colorMaterialIndigo,null))
                    }
                }
                "reschedule" -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        txt_tktStatus.setTextColor(this.resources.getColor(R.color.colorMaterialPink,null))
                    }
                }
                "not accepted" -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        txt_tktStatus.setTextColor(this.resources.getColor(R.color.colorBlue,null))
                    }
                }
                "urgent ticket" -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        txt_tktStatus.setTextColor(this.resources.getColor(R.color.colorRed,null))
                    }
                }
                "inprogress ticket" -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        txt_tktStatus.setTextColor(this.resources.getColor(R.color.material_teal_700,null))
                    }
                }
                "closed" -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        txt_tktStatus.setTextColor(this.resources.getColor(R.color.colorMaterialLime,null))
                    }
                }
                "reassign" -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        txt_tktStatus.setTextColor(this.resources.getColor(R.color.colorMaterialAmber,null))
                    }
                }
                else -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        txt_tktStatus.setTextColor(this.resources.getColor(R.color.colorMaterialGreen,null))
                    }
                }
            }

            txt_tktStatus.text = tktStatus

//            txt_warranty_uptoDate.text = modelItem?.WarrantyUptoDate
//            txt_due_by_date.text = modelItem?.DueByDate

            if(modelItem.InWarranty == true){
                txt_in_warranty.text = "YES"
            }else{
                txt_in_warranty.text = "NO"
            }

            txt_engg_instruction.text = modelItem.EngineerInstructions

            if(modelItem.IsSCAddressverified == true){
                txt_sc_address_verified.text = "YES"
            }else{
                txt_sc_address_verified.text = "NO"
            }
            txt_appointment_date.text = modelItem.AppointmentDate
        }

        Glide.with(this)
                .load(modelItem?.BillPhotoProof)
                .fitCenter()
                .placeholder(R.drawable.no_image_icon)
                .into(imgBill)

        Glide.with(this)
                .load(modelItem?.SelfieImage)
                .fitCenter()
                .placeholder(R.drawable.no_image_icon)
                .into(imgSelfie)

        Glide.with(this)
                .load(modelItem?.QRImage)
                .fitCenter()
                .placeholder(R.drawable.no_image_icon)
                .into(imgQR)

        Glide.with(this)
                .load(modelItem?.ProductImage)
                .fitCenter()
                .placeholder(R.drawable.no_image_icon)
                .into(imgProduct)

    }


    override fun onStarted(callFrom: String) {
        //TODO("Not yet implemented")
    }

    override fun onSuccess(_object: List<Any?>, callFrom: String) {
        //  TODO("Not yet implemented")


        val data = _object as List<GetTicketDetailsData?>

        if(data.isNotEmpty()){
            bindUI(data[0])
            clickListener(data[0])
        }
    }


    fun splitStr(strLink: String): String {
        if (!strLink.isNullOrEmpty()) {
            val tokens: List<String> = strLink.split("/")
            if(tokens.count() > 0){
                var strPath = tokens[tokens.count() - 1]
                return strPath
            }
            return "crm.jpg"
        }else{
            return "crm.jpg"
        }
    }

    private fun clickListener(modelItem: GetTicketDetailsData?) {

        binding.txtProductView?.setOnClickListener {
            if(!modelItem?.ProductImage.isNullOrEmpty()) {
                val intent = Intent(this, FullscreenImageActivity::class.java)
                        .putExtra(ARG_IMAGE_URL, modelItem?.ProductImage)
                startActivity(intent)
            }
        }

        binding.txtProductDownload?.setOnClickListener {
            downloadFile(this,modelItem?.ProductImage ?: "",splitStr(modelItem?.ProductImage ?: ""))
        }

        binding.txtQrView?.setOnClickListener {
            if(!modelItem?.QRImage.isNullOrEmpty()){
                val intent = Intent(this, FullscreenImageActivity::class.java)
                        .putExtra(ARG_IMAGE_URL, modelItem?.QRImage)
                startActivity(intent)
            }

        }

        binding.txtQrDownload?.setOnClickListener {
                downloadFile(this,modelItem?.QRImage ?: "",splitStr(modelItem?.QRImage ?: ""))
        }

        binding.txtBillView?.setOnClickListener {
            if(!modelItem?.BillPhotoProof.isNullOrEmpty()){
            val intent = Intent(this, FullscreenImageActivity::class.java)
                    .putExtra(ARG_IMAGE_URL, modelItem?.BillPhotoProof)
            startActivity(intent)
            }
        }

        binding.txtBillDownload?.setOnClickListener {
            downloadFile(this,modelItem?.BillPhotoProof ?: "",splitStr(modelItem?.BillPhotoProof ?: ""))
        }

        binding.txtSelfieView?.setOnClickListener {
            if(!modelItem?.SelfieImage.isNullOrEmpty()) {
                val intent = Intent(this, FullscreenImageActivity::class.java)
                        .putExtra(ARG_IMAGE_URL, modelItem?.SelfieImage)
                startActivity(intent)
            }
        }

        binding.txtSelfieDownload?.setOnClickListener {
            downloadFile(this,modelItem?.SelfieImage ?: "",splitStr(modelItem?.SelfieImage ?: ""))
        }

    }


    override fun onError(message: String, callFrom: String, isNetworkError: Boolean) {
        // TODO("Not yet implemented")
    }

    override fun onValidationError(message: String, callFrom: String) {
        //TODO("Not yet implemented")
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        private const val ARG_TICKET_ID = "ticket_id"
        private const val ARG_TICKET_NO = "ticket_no"
        private const val ARG_IMAGE_URL = "image_url"

        //        private const val REFRESH_REQUEST_CODE = 101
//        private const val REFRESH_RESULT_CODE = 102
        private const val ARG_PARAM_ITEM = "model_item"
        fun start(context: Context, ticketId: Int, ticketNo: String?) {
            val intent = Intent(context, TicketViewDetailsActivity::class.java)
            intent.putExtra(ARG_TICKET_ID, ticketId)
            intent.putExtra(ARG_TICKET_NO, ticketNo)
            context.startActivity(intent)

        }
    }
}