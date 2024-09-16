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
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.util.*

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

            binding.txtTktNo.text = modelItem.TicketNo
            binding.txtDivName.text = modelItem.DivisionName
            binding.txtPriority.text = modelItem.TicketPriority
            binding.txtDate.text = modelItem.TicketDate
            binding.txtCustName.text = modelItem.CustName
            binding.txtRescheduleDate.text = modelItem.ReScheduleDate + "  " + modelItem.ReScheduledByName
            binding.txtRescheduleRemark.text = modelItem.ReScheduleRemark
            binding.txtContactNo.text = modelItem.CustContactNo
            binding.txtCustAddress.text = modelItem.CustAddress + ", " + modelItem.City + ", " + modelItem.Distrctnm + ", " + modelItem.statenm + ", " + modelItem.Pincode
            binding.txtCustEmailId.text = modelItem.EmailID
            binding.txtItemName.text = modelItem.ProductName
            binding.txtManufactureDate.text = modelItem.ManufactureDate
            binding.txtTimeSlot.text = modelItem.TimeSlot
            binding.txtProdWarranty.text = modelItem.WarrantyUptoDate
            binding.txtItemQrCode.text = modelItem.ItemQRCode
            binding.txtItemEanNo.text = modelItem.ItemEANNo
            binding.txtItemProductCode.text = ""
            binding.txtItemPurchaseDate.text = modelItem.PurchaseDt
            binding.txtItemIssues.text = modelItem.ProductIssues
            binding.txtItemDescr.text = modelItem.ProductIssueDesc
            binding.txtPartyName.text = modelItem.PartyName + "  " + modelItem.PartyTypeName
          //  txt_remarks.text = modelItem?.AssignRemark
            binding.txtPartyAddress.text = modelItem.PartyAddress
            binding.txtSymptoms.text = modelItem.Symptoms.ifEmpty { "-" }
            binding.txtDefectReason.text = modelItem.DefectReason.ifEmpty { "-" }
            binding.txtRepairActionType.text = modelItem.RepairActionType.ifEmpty { "-" }
            binding.txtRepairType.text = modelItem.RepairType.ifEmpty { "-" }
            binding.txtReplacementReason.text = modelItem.ReplacementReason.ifEmpty { "-" }

            if(modelItem.IsCheckedIn == 1){
                binding.txtCheckedIn.text = "YES"
            }else{
                binding.txtCheckedIn.text = "NO"
            }

            if(modelItem.IsDealerCall){
                binding.txtDealerCall.text = "YES"
            }else{
                binding.txtDealerCall.text = "NO"
            }

            if(!modelItem.IsNoRepair){
                binding.txtDealerIsNoRepair.text = "YES"
            }else{
                binding.txtDealerIsNoRepair.text = "NO"
            }

            if(!modelItem.IsInvoiceGenrated){
                binding.txtDealerIsNoRepair.text = "-"
            }

            if(modelItem.TicketStatus?.lowercase(Locale.getDefault()).equals("closed")){
                binding.llImgMain.visibility = View.VISIBLE
            }else{
                binding.llImgMain.visibility = View.GONE
            }


            val tktStatus= modelItem.TicketStatus

            when (tktStatus?.lowercase(Locale.getDefault())) {

                "pending ticket" -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        binding.txtTktStatus.setTextColor(this.resources.getColor(R.color.colorYellow,null))
                    }
                }
                "visited" -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        binding.txtTktStatus.setTextColor(this.resources.getColor(R.color.colorMaterialIndigo,null))
                    }
                }
                "reschedule" -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        binding.txtTktStatus.setTextColor(this.resources.getColor(R.color.colorMaterialPink,null))
                    }
                }
                "not accepted" -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        binding.txtTktStatus.setTextColor(this.resources.getColor(R.color.colorBlue,null))
                    }
                }
                "urgent ticket" -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        binding.txtTktStatus.setTextColor(this.resources.getColor(R.color.colorRed,null))
                    }
                }
                "inprogress ticket" -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        binding.txtTktStatus.setTextColor(this.resources.getColor(R.color.material_teal_700,null))
                    }
                }
                "closed" -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        binding.txtTktStatus.setTextColor(this.resources.getColor(R.color.colorMaterialLime,null))
                    }
                }
                "reassign" -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        binding.txtTktStatus.setTextColor(this.resources.getColor(R.color.colorMaterialAmber,null))
                    }
                }
                else -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        binding.txtTktStatus.setTextColor(this.resources.getColor(R.color.colorMaterialGreen,null))
                    }
                }
            }

            binding.txtTktStatus.text = tktStatus

//            txt_warranty_uptoDate.text = modelItem?.WarrantyUptoDate
//            txt_due_by_date.text = modelItem?.DueByDate

            if(modelItem.InWarranty == true){
                binding.txtInWarranty.text = "YES"
            }else{
                binding.txtInWarranty.text = "NO"
            }

            binding.txtEnggInstruction.text = modelItem.EngineerInstructions

            if(modelItem.IsSCAddressverified == true){
                binding.txtScAddressVerified.text = "YES"
            }else{
                binding.txtScAddressVerified.text = "NO"
            }
            binding.txtAppointmentDate.text = modelItem.AppointmentDate
        }

        Glide.with(this)
                .load(modelItem?.BillPhotoProof)
                .fitCenter()
                .placeholder(R.drawable.no_image_icon)
                .into(binding.imgBill)

        Glide.with(this)
                .load(modelItem?.SelfieImage)
                .fitCenter()
                .placeholder(R.drawable.no_image_icon)
                .into(binding.imgSelfie)

        Glide.with(this)
                .load(modelItem?.QRImage)
                .fitCenter()
                .placeholder(R.drawable.no_image_icon)
                .into(binding.imgQR)

        Glide.with(this)
                .load(modelItem?.ProductImage)
                .fitCenter()
                .placeholder(R.drawable.no_image_icon)
                .into(binding.imgProduct)

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


    private fun splitStr(strLink: String): String {
        if (strLink.isNotEmpty()) {
            val tokens: List<String> = strLink.split("/")
            if(tokens.isNotEmpty()) {
                return tokens[tokens.count() - 1]
            }
            return "crm.jpg"
        }else{
            return "crm.jpg"
        }
    }

    private fun clickListener(modelItem: GetTicketDetailsData?) {

        binding.txtProductView.setOnClickListener {
            if(!modelItem?.ProductImage.isNullOrEmpty()) {
                val intent = Intent(this, FullscreenImageActivity::class.java)
                    .putExtra(ARG_IMAGE_URL, modelItem?.ProductImage)
                startActivity(intent)
            }
        }

        binding.txtProductDownload.setOnClickListener {
            downloadFile(this,modelItem?.ProductImage ?: "",splitStr(modelItem?.ProductImage ?: ""))
        }

        binding.txtQrView.setOnClickListener {
            if(!modelItem?.QRImage.isNullOrEmpty()){
                val intent = Intent(this, FullscreenImageActivity::class.java)
                    .putExtra(ARG_IMAGE_URL, modelItem?.QRImage)
                startActivity(intent)
            }

        }

        binding.txtQrDownload.setOnClickListener {
            downloadFile(this,modelItem?.QRImage ?: "",splitStr(modelItem?.QRImage ?: ""))
        }

        binding.txtBillView.setOnClickListener {
            if(!modelItem?.BillPhotoProof.isNullOrEmpty()){
                val intent = Intent(this, FullscreenImageActivity::class.java)
                    .putExtra(ARG_IMAGE_URL, modelItem?.BillPhotoProof)
                startActivity(intent)
            }
        }

        binding.txtBillDownload.setOnClickListener {
            downloadFile(this,modelItem?.BillPhotoProof ?: "",splitStr(modelItem?.BillPhotoProof ?: ""))
        }

        binding.txtSelfieView.setOnClickListener {
            if(!modelItem?.SelfieImage.isNullOrEmpty()) {
                val intent = Intent(this, FullscreenImageActivity::class.java)
                    .putExtra(ARG_IMAGE_URL, modelItem?.SelfieImage)
                startActivity(intent)
            }
        }

        binding.txtSelfieDownload.setOnClickListener {
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