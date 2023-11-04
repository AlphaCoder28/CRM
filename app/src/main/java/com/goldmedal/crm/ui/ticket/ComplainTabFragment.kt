package com.goldmedal.crm.ui.ticket


import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.text.SpannableString
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.goldmedal.crm.R
import com.goldmedal.crm.common.ApiStageListener
import com.goldmedal.crm.common.FullscreenImageActivity
import com.goldmedal.crm.common.ImageSelectionListener
import com.goldmedal.crm.common.LocationManager.GPSPoint
import com.goldmedal.crm.common.LocationManager.Wherebout
import com.goldmedal.crm.common.LocationManager.Workable
import com.goldmedal.crm.data.model.*
import com.goldmedal.crm.data.network.GlobalConstant.FULL_IMAGE_SIZE
import com.goldmedal.crm.data.network.GlobalConstant.THUMBNAIL_SIZE
import com.goldmedal.crm.data.network.responses.ReplacementReasonItem
import com.goldmedal.crm.databinding.ComplainTabFragmentBinding
import com.goldmedal.crm.ui.auth.WebActivity
import com.goldmedal.crm.ui.invoice.GenerateInvoiceActivity
import com.goldmedal.crm.ui.parts.PartsRequirementActivity
import com.goldmedal.crm.ui.ticket.scanner.QrCodeScanActivity
import com.goldmedal.crm.ui.ticket.scanner.QrCodeScanActivity.Companion.RESULT_REQUEST_CODE
import com.goldmedal.crm.util.*
import com.goldmedal.crm.util.interfaces.OnRefreshListener
import com.goldmedal.hrapp.ui.dialogs.TicketOTPDialog
import com.google.android.material.chip.Chip
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.annotations.AfterPermissionGranted
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.complain_tab_fragment.*
import kotlinx.android.synthetic.main.product_info.*
import org.angmarch.views.OnSpinnerItemSelectedListener
import org.angmarch.views.SpinnerTextFormatter
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.io.File
import java.util.*
import kotlin.reflect.typeOf


//<!--added by akshay-->


private const val ARG_PARAM = "model_item"
private const val TAG = "ComplainTabFragment"


class ComplainTabFragment : Fragment(), KodeinAware, ApiStageListener<Any>,
    OptionsBottomSheetFragment.ItemClickListener, ImageSelectionListener,
    TicketOTPDialog.OnOTPReceived, OnRefreshListener {

    private var strInput: String = ""
    private var modelItem: GetTicketDetailsData? = null

    private var resendOtp = 0
    private var strDeviceId = ""

    override val kodein by kodein()
    private val factory: TicketViewModelFactory by instance()
    private lateinit var viewModel: TicketViewModel

    private var _binding: ComplainTabFragmentBinding? = null
    private val binding get() = _binding!!

    private var latitude: Double? = 0.0
    private var longitude: Double? = 0.0

    var outsidePremises: Boolean? = false
    var strPremisesRemark: String? = "-"
    var strCheckoutDistance: String? = "0.0"

    var intSearchType = 1
    var passSearchType = 1
    var invGenerated = false
    var isNoRepair = false
    var isReplacementRequired = false
    var qrCode = ""

    // - - - - 1 - bill, 2- product , 3 - qr , 4- selfie, 5- replacement
    var uploadCallFrom = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            modelItem = it.getParcelable(ARG_PARAM)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ComplainTabFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, factory).get(TicketViewModel::class.java)
        viewModel.apiListener = this

        bindUI()
        clickListeners()
        strDeviceId = getDeviceId(requireContext()) ?: ""

        invGenerated = modelItem?.IsInvoiceGenrated ?: true

        if (invGenerated) {
            binding.layoutProductInfo.llNoRepairMain.visibility = View.GONE
        }

        var itemQrCode = modelItem?.ItemQRCode ?: ""
        var itemEanNo = modelItem?.ItemEANNo ?: ""

        if (!itemQrCode.isEmpty()) {
            qrCode = itemQrCode
            intSearchType = 1
            passSearchType = intSearchType
        }

        if (itemQrCode.isEmpty() && !itemEanNo.isEmpty()) {
            qrCode = itemEanNo
            intSearchType = 2
            passSearchType = intSearchType
        }

        if (itemQrCode.isEmpty() && itemEanNo.isEmpty()) {
            qrCode = ""
            intSearchType = 1
            passSearchType = intSearchType
        }

        if (!qrCode.isEmpty()) {
            if (modelItem?.CustomerID ?: 0 == 0) {
                Toast.makeText(requireContext(), "Invalid Customer", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.searchQrCode(
                    qrCode,
                    modelItem?.CustomerID ?: 0,
                    intSearchType,
                    modelItem?.TicketID ?: 0,
                    false
                )
            }
        }

        binding.layoutProductInfo.radioButtonNoRepair.isChecked = false
        binding.layoutProductInfo.radioButtonYesRepair.isChecked = true

        binding.layoutProductInfo.radioBtnNoGenerateAgain.isChecked = true
        binding.layoutProductInfo.radioBtnYesGenerateAgain.isChecked = false

        if (binding.layoutProductInfo.radioButtonNoRepair.isChecked) {
            isNoRepair = true
            binding.layoutProductInfo.llGenerateInvoiceView.visibility = View.GONE
        } else {
            isNoRepair = false
            binding.layoutProductInfo.llGenerateInvoiceView.visibility = View.VISIBLE
        }


        binding.layoutProductInfo.radioButtonNoReplacement.isChecked = true
        binding.layoutProductInfo.radioButtonYesReplacement.isChecked = false

        if (binding.layoutProductInfo.radioButtonNoReplacement.isChecked) {
            isReplacementRequired = false
            binding.layoutProductInfo.llUploadReplacementQrCode.visibility = View.GONE
        } else {
            isReplacementRequired = true
            binding.layoutProductInfo.llUploadReplacementQrCode.visibility = View.VISIBLE
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    // ============================================================================================
    //  API CALL
    // ============================================================================================

    override fun onStarted(callFrom: String) {
        binding.progressBar.start()
    }


    override fun onSuccess(_object: List<Any?>, callFrom: String) {
        binding.progressBar.stop()

        if (callFrom == "product_info_scan" || callFrom == "product_info_search") {
            val qrScanData = _object as List<ProductInfoData>
            if(qrScanData.isNotEmpty()) {
                viewModel.strDateOfPurchase = qrScanData[0].PurchaseDt
                binding.layoutProductInfo.txtDOP.text = viewModel.strDateOfPurchase
            }

            if (callFrom == "product_info_search" && strInput.isNotEmpty()) {
                passSearchType = intSearchType
                qrCode = strInput
            }

            if (callFrom == "product_info_scan") {
                passSearchType = 1
            }

            bindProductInfo(qrScanData)
        }
        if (callFrom == "product_symptoms_list") {
            val symptomsData = _object as List<SymptomsData>
            setCategoryChips(symptomsData)

        }
        if (callFrom == "update_ticket_status") {
            val updateStatusData = _object as List<UpdateVisitStatusData>
            showSuccessAlert(updateStatusData[0].ActionIDStatus)
        }
        if (callFrom == "time_slots") {
            val timeSlots = _object as MutableList<GetTimeSlots?>?
            timeSlots?.add(0, GetTimeSlots("Select", -1))
            bindTimeSlots(timeSlots)
        }

        if (callFrom == "close_otp") {
            Log.d(
                "INV GENERATED - - - -",
                "$invGenerated - - - - $isNoRepair"
            )
            if (invGenerated || isNoRepair) {
                closeTicketOtpPopup(_object as List<closedOtpData>?)
            } else {
                binding.rootLayout.snackbar("Please Generate Invoice first before closing the Ticket")
            }

        }

        if (callFrom == GET_REPLACEMENT_REASONS_LIST) {
            val replacementData = _object as MutableList<ReplacementReasonItem?>?
            replacementData?.add(0, ReplacementReasonItem("Select", 0))
            bindReplacementReasons(replacementData)
        }

        if (callFrom == GET_SYMPTOMS_LIST_NEW) {
            val symptomsData = _object as MutableList<ProductSymptomsItem?>?
            symptomsData?.add(0, ProductSymptomsItem("Select", 0))
            bindSymptomsSpinner(symptomsData)
        }

        if (callFrom == GET_DEFECT_REASON_LIST) {
            val defectReasonData = _object as MutableList<DefectReasonItem?>?
            defectReasonData?.add(0, DefectReasonItem("Select", 0))
            bindDefectReasonSpinner(defectReasonData)
        }

        if (callFrom == GET_REPAIR_ACTION_DETAILS) {
            val repairActionData = _object as MutableList<RepairActionDetailItem?>?
            repairActionData?.add(0, RepairActionDetailItem("Select", 0))
            bindRepairActionDetailSpinner(repairActionData)
        }

        if (callFrom == GET_REPAIR_TYPE_LIST) {
            val repairTypeData = _object as MutableList<RepairTypeItem?>?
            repairTypeData?.add(0, RepairTypeItem("Select", 0))
            bindRepairTypeSpinner(repairTypeData)
        }
    }


    override fun onError(message: String, callFrom: String, isNetworkError: Boolean) {
        binding.progressBar.stop()
        binding.rootLayout.snackbar(message)
    }


    override fun onValidationError(message: String, callFrom: String) {
        binding.rootLayout.snackbar(message)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESULT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            val slNoKey = data?.getStringExtra("key")
            val qrCode = data?.getStringExtra("qr_code")
            val callFrom = data?.getStringExtra("callFrom")
            val qrMaster = data?.getBooleanExtra("master", false)
            Log.d("CALLFROM - - -", "onActivityResult: $callFrom")

            if(callFrom.equals("scan")){
                viewModel.strQrCode = qrCode
                viewModel.master = qrMaster

                Coroutines.main {
                    if ((modelItem?.CustomerID ?: 0) == 0) {
                        Toast.makeText(requireContext(), "Invalid Customer", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.scanQrCode(
                            slNoKey,
                            qrCode,
                            modelItem?.CustomerID ?: 0,
                            qrMaster ?: false
                        )
                    }
                }

            }


            if(callFrom.equals("replacement")){
                viewModel.strReplacementImage = qrCode

                if(qrCode.isNullOrEmpty()){
                    binding.layoutProductInfo.txtQrCodeReplacementTitle.text = ""
                }else{
                    binding.layoutProductInfo.txtQrCodeReplacementTitle.text = viewModel.strReplacementImage
                }

            }


            Log.d(
                TAG,
                "onActivityResult: slNoKey: " + slNoKey + "qr code: " + qrCode + "master: " + qrMaster
            )
        } else if (requestCode == RC_CAMERA_PERM) {
            if (data != null) {
                val thumbnail = data.extras!!.get("data") as Bitmap
                prepareImgUpload(thumbnail)
            }
        } else if (requestCode == RC_STORAGE_PERM && resultCode == Activity.RESULT_OK) {
            val uri: Uri
            if (data != null) {
                uri = data.data!!

                val filePath = getPathFromUri(requireContext(), uri)
//                assert uri != null;
//                filePath = uri.getPath();
                if (filePath != null) {
                    val file = File(filePath)

                    // Get length of file in bytes
                    val fileSizeInBytes = file.length()
                    // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
                    val fileSizeInKB = fileSizeInBytes / 1024
                    // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
                    val fileSizeInMB = fileSizeInKB / 1024
                    if (fileSizeInMB > 4) {
                        binding.rootLayout.snackbar("Cannot attach file more than 4 Mb")
                        return
                    }

                    if (uploadCallFrom == 1) {
                        prepareDocumentUpload(filePath)
                    } else {
                        val thumbnail = getBitmap(filePath)
                        if (thumbnail != null) {
                            prepareImgUpload(thumbnail)
                        } else {
                            Toast.makeText(requireContext(), "No Image Found", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }

                }


            }

        }
    }

    override fun choosePhotoFromGallery() {
        onClickRequestPermissionStorageButton()
    }

    override fun takePhotoFromCamera() {
        onClickRequestPermissionCameraButton()
    }

    override fun onItemClick(param: String) {
        when (param) {
            "scan" -> {
                QrCodeScanActivity.start(requireContext(), this,"scan")
            }
            "search" -> {
                searchQRCode(1)
            }
            "eanNo" -> {
                searchQRCode(2)
            }
            "productCode" -> {
                searchQRCode(3)
            }
            else -> {
                // (Nanimoshinai)
            }
        }
    }


    // ============================================================================================
    //  Private Methods
    // ============================================================================================

    fun closeTicketOtpPopup(list: List<closedOtpData>?) {

        if (list?.get(0)?.GoodOtp?.isEmpty() == true) {
            updateTicketAPI()
        } else {
            if(resendOtp == 0){
                val dialogFragment = TicketOTPDialog.newInstance()
                dialogFragment.strGoodOtp = list?.get(0)?.GoodOtp ?: ""
                dialogFragment.strBadOtp = list?.get(0)?.BadOTP ?: ""
                dialogFragment.callBack = this

                val ft = requireActivity().supportFragmentManager.beginTransaction()
                val prev = requireActivity().supportFragmentManager.findFragmentByTag("otp_tkt_dialog")
                if (prev != null) {
                    ft.remove(prev)
                }
                ft.addToBackStack(null)
                dialogFragment.show(ft, "otp_tkt_dialog")
            }

        }

    }


    private fun bindUI() = Coroutines.main {
        modelItem?.let {
            binding.layoutComplainTicket.txtTicketNo.text = modelItem?.TicketNo
            binding.layoutComplainTicket.txtTimeSlot.text =
                modelItem?.AppointmentDate + " | " + modelItem?.TimeSlot
            binding.layoutComplainTicket.txtCustName.text = modelItem?.CustName
            binding.layoutComplainTicket.txtAddress.text = modelItem?.CustAddress
            binding.layoutComplainTicket.txtProductIssue.text = modelItem?.ProductIssues
            binding.layoutComplainTicket.textViewStatus.text = modelItem?.TicketStatus

            binding.layoutProductInfo.edtSlMain.isVisible = false
            binding.layoutProductInfo.llRemarkEan.isVisible = true
            viewModel.isSlNoAvailable = false
            binding.layoutProductInfo.edtEanRemark.setText("")

            // - - - To show generate invoice or not - - - -
            if (modelItem?.IsInvoiceGenrated == true) {
                binding.layoutProductInfo.btnGenerateInvoice.visibility = View.GONE
                binding.layoutProductInfo.txtPdf.visibility = View.VISIBLE
                binding.btnScanQr.visibility = View.GONE
                binding.layoutProductInfo.tvScanAgain.visibility = View.GONE

                binding.layoutProductInfo.llGenerateInvoiceAgainView.visibility = View.VISIBLE
            } else {
                binding.layoutProductInfo.btnGenerateInvoice.visibility = View.VISIBLE
                binding.layoutProductInfo.txtPdf.visibility = View.GONE
                binding.btnScanQr.visibility = View.VISIBLE
                binding.layoutProductInfo.tvScanAgain.visibility = View.GONE

                binding.layoutProductInfo.llGenerateInvoiceAgainView.visibility = View.GONE
            }

//            binding.layoutProductInfo.radioEanYes.isChecked = true
//            binding.layoutProductInfo.radioEanNo.isChecked = false

            if (modelItem?.isFreeService == true) {
                binding.layoutComplainTicket.txtFreeService.visibility = View.VISIBLE
            }
            if (listOf(
                    "high",
                    "urgent"
                ).contains(modelItem?.TicketPriority?.toLowerCase(Locale.getDefault()))
            ) {
//            if (modelItem?.TicketPriority?.equals("high", ignoreCase = true) == true) {
                binding.layoutComplainTicket.textViewPriority.visibility = View.VISIBLE
                binding.layoutComplainTicket.textViewPriority.text =
                    requireContext().getString(R.string.str_urgent)
            }


            //Product Info
            binding.layoutProductInfo.txtProductName.text = modelItem?.ProductName
            binding.layoutProductInfo.txtDivisionName.text = modelItem?.DivisionName
            binding.layoutProductInfo.edtSlNo.setText(modelItem?.ItemSerialNo)

            viewModel.productId = modelItem?.ProductID
            viewModel.divisionId = modelItem?.DivisionID
            viewModel.categoryId = modelItem?.CategoryID
            viewModel.strDateOfPurchase = modelItem?.PurchaseDt ?: ""
            // formatDateString(modelItem?.PurchaseDt ?: "", "dd/MM/yyyy", "MM-dd-yyyy")

            when (modelItem?.InWarranty) {
                true -> binding.layoutProductInfo.radioButtonYes.isChecked = true
                else -> binding.layoutProductInfo.radioButtonNo.isChecked = true
            }

            // lock warranty selection
            if (modelItem?.WarrantyUptoDate?.isNotEmpty() == true) {
                binding.layoutProductInfo.radioButtonYes.isEnabled = false
                binding.layoutProductInfo.radioButtonNo.isEnabled = false
            }

//            when (viewModel?.isEanNoAvailable) {
//                true -> binding.layoutProductInfo.radioEanYes.isChecked = true
//                else -> binding.layoutProductInfo.radioEanNo.isChecked = true
//            }

            if (!modelItem?.PurchaseDt.isNullOrEmpty()) {
                binding.layoutProductInfo.txtDOP.text = modelItem?.PurchaseDt
            }

            //Complain Info
            binding.layoutComplainInfo.textViewComplainTitle.text = modelItem?.ProductIssues
            binding.layoutComplainInfo.textViewComplainSubtitle.text = modelItem?.ProductIssueDesc
            binding.layoutComplainInfo.textViewInstructionsSubtitle.text =
                modelItem?.EngineerInstructions

            viewModel.isNewSymptomsBind = modelItem?.IsNewSymptomsBind ?: false
            if (modelItem?.IsNewSymptomsBind == true) {
                viewModel.getNewSymptomsList(modelItem?.CategoryID, modelItem?.DivisionID)
                binding.layoutComplainInfo.spinnerSymptoms.visibility = View.VISIBLE
            } else {
                viewModel.getSymptomsList(modelItem?.CategoryID)
            }


        }
        //headers

        binding.layoutProductInfoHeader.itemExpandableHeaderTitle.setText(R.string.str_product_info)
        binding.layoutComplainInfoHeader.itemExpandableHeaderTitle.setText(R.string.str_complain_info)
        binding.layoutAccountsInfoHeader.itemExpandableHeaderTitle.setText(R.string.str_accounts_info)

        // show wiring device header based on division id (1 - WD)
        if (modelItem?.DivisionID == 1) {
            binding.layoutWiringDevicesHeader.itemExpandableHeaderRoot.visibility = View.VISIBLE
            binding.layoutWiringDevicesHeader.itemExpandableHeaderTitle.text = getString(R.string.wiring_devices_txt)
        }


        //Visit Status
        val visitStatus: MutableList<VisitStatusData> = ArrayList()

        visitStatus.add(VisitStatusData("Select", -1))
        visitStatus.add(VisitStatusData("Re-Assign", 1))
        visitStatus.add(VisitStatusData("Re-Schedule", 2))
        visitStatus.add(VisitStatusData("Close", 3))

        val textFormatter =
            SpinnerTextFormatter<VisitStatusData> { obj ->
                SpannableString(
                    obj.VisitStatus
                )
            }
        binding.spinnerVisitStatus.setSpinnerTextFormatter(textFormatter)
        binding.spinnerVisitStatus.setSelectedTextFormatter(textFormatter)

        binding.spinnerVisitStatus.onSpinnerItemSelectedListener =
            OnSpinnerItemSelectedListener { parent, view, position, id ->
                val item = binding.spinnerVisitStatus.selectedItem as VisitStatusData

                if (item.ActionId == -1) {
                    binding.layoutEngineerRemark.remarksRoot.visibility = View.GONE
                    binding.layoutReschedule.rescheduleRoot.visibility = View.GONE
                    binding.llCloseType.visibility = View.GONE
                    binding.llReplacementReason.visibility = View.GONE
                } else if (item.ActionId == 1) {
                    binding.layoutEngineerRemark.remarksRoot.visibility = View.VISIBLE
                    binding.layoutReschedule.rescheduleRoot.visibility = View.GONE
                    binding.llCloseType.visibility = View.GONE
                    binding.llReplacementReason.visibility = View.GONE
                } else if (item.ActionId == 2) {
                    binding.layoutEngineerRemark.remarksRoot.visibility = View.VISIBLE
                    binding.layoutReschedule.rescheduleRoot.visibility = View.VISIBLE
                    binding.llCloseType.visibility = View.GONE
                    binding.llReplacementReason.visibility = View.GONE
                    viewModel.getTimeSlots()

                } else {
                    binding.layoutEngineerRemark.remarksRoot.visibility = View.VISIBLE
                    binding.llCloseType.visibility = View.VISIBLE
                    binding.layoutReschedule.rescheduleRoot.visibility = View.GONE
                }
                viewModel.actionId = item.ActionId ?: -1
            }
        binding.spinnerVisitStatus.attachDataSource(visitStatus)

        // Call Close with spinner
        val callCloseWith: MutableList<VisitStatusData> = ArrayList()

        callCloseWith.add(VisitStatusData("Select", -1))
        callCloseWith.add(VisitStatusData("With Consumption", 1))
        callCloseWith.add(VisitStatusData("Without Consumption", 2))
        callCloseWith.add(VisitStatusData("Replacement", 3))

        val textFormatter1 =
            SpinnerTextFormatter<VisitStatusData> { obj ->
                SpannableString(
                    obj.VisitStatus
                )
            }
        binding.spinnerCallClose.setSpinnerTextFormatter(textFormatter1)
        binding.spinnerCallClose.setSelectedTextFormatter(textFormatter1)

        binding.spinnerCallClose.onSpinnerItemSelectedListener =
            OnSpinnerItemSelectedListener { parent, view, position, id ->
                val item = binding.spinnerCallClose.selectedItem as VisitStatusData
                viewModel.callCloseTypeId = item.ActionId ?: -1
                when (item.ActionId) {
                    1 -> {
                        binding.llReplacementReason.visibility = View.GONE
                        viewModel.replacementReasonId = 0
                    }
                    2 -> {
                        binding.llReplacementReason.visibility = View.GONE
                        viewModel.replacementReasonId = 0
                    }
                    3 -> {
                        modelItem?.TicketID?.let { viewModel.getReplacementReasonsList(it) }
                        binding.llReplacementReason.visibility = View.VISIBLE
                    }
                }
            }
        binding.spinnerCallClose.attachDataSource(callCloseWith)
    }


    private fun bindProductInfo(list: List<ProductInfoData?>?) = Coroutines.main {

        list?.let {

            viewModel.divisionId = list[0]?.divisionid
            viewModel.categoryId = list[0]?.categoryid
            viewModel.productId = list[0]?.ProductID

            binding.layoutProductInfo.txtProductName.text = list[0]?.ProductName
            binding.layoutProductInfo.txtDivisionName.text = list[0]?.divisionnm
            binding.layoutProductInfo.txtProductDescription.text = list[0]?.ProductDescription

            binding.layoutProductInfo.imvQrPlaceHolder.visibility = View.VISIBLE
            binding.layoutProductInfo.txtQrCode.text = viewModel.strQrCode

            if (viewModel.productId != null) {
                qrCode = list[0]?.QRCode ?: ""
                binding.layoutProductInfo.edtSlNo.setText(list[0]?.QRCode)
                binding.layoutProductInfo.edtSlMain.isVisible = true
                binding.layoutProductInfo.llRemarkEan.isVisible = false
                viewModel.isSlNoAvailable = true
            } else {
                binding.layoutProductInfo.edtSlMain.isVisible = false
                binding.layoutProductInfo.llRemarkEan.isVisible = true
                viewModel.isSlNoAvailable = false
                binding.layoutProductInfo.edtEanRemark.setText("")
            }
            //  Toast.makeText(requireContext(), "passSearchType - - -"+passSearchType.toString(), Toast.LENGTH_SHORT).show()
            // - - - - - - change text for qr,ean,product code header - - - - -
            if (passSearchType == 1) {
                binding.layoutProductInfo.edtSlMain.hint = "Item QR Code"
                binding.layoutProductInfo.edtSlNo.setText(qrCode)
            }

            if (passSearchType == 2) {
                binding.layoutProductInfo.edtSlMain.hint = "Item EAN No"
                binding.layoutProductInfo.edtSlNo.setText(qrCode)
            }

            if (passSearchType == 3) {
                binding.layoutProductInfo.edtSlMain.hint = "Item Product Code"
                binding.layoutProductInfo.edtSlNo.setText(qrCode)
            }


            openProductInfo()

            if (modelItem?.IsNewSymptomsBind == true) {
                viewModel.getNewSymptomsList(modelItem?.CategoryID, modelItem?.DivisionID)
                binding.layoutComplainInfo.spinnerSymptoms.visibility = View.VISIBLE
            } else {
                viewModel.getSymptomsList(categoryId = list[0]?.categoryid)
            }

        }

    }


    private fun bindComplainInfo(list: List<ProductInfoData?>?) = Coroutines.main {

        list?.let {

        }

    }


    private fun bindTimeSlots(list: MutableList<GetTimeSlots?>?) {

        list?.let {
            val textFormatter =
                SpinnerTextFormatter<GetTimeSlots> { obj ->
                    SpannableString(
                        obj.TimeSlot
                    )
                }
            binding.layoutReschedule.spinnerTimeSlot.setSpinnerTextFormatter(textFormatter)
            binding.layoutReschedule.spinnerTimeSlot.setSelectedTextFormatter(textFormatter)

            binding.layoutReschedule.spinnerTimeSlot.onSpinnerItemSelectedListener =
                OnSpinnerItemSelectedListener { parent, view, position, id ->
                    val item = binding.layoutReschedule.spinnerTimeSlot.selectedItem as GetTimeSlots
                    viewModel.timeSlotId = item.TimeSlotID
                }

            binding.layoutReschedule.spinnerTimeSlot.attachDataSource(list)
        }

    }

    private fun bindReplacementReasons(replacementData: MutableList<ReplacementReasonItem?>?) {

        replacementData?.let {
            val textFormatter1 =
                SpinnerTextFormatter<ReplacementReasonItem> { obj ->
                    SpannableString(
                        obj.replacementReason
                    )
                }
            binding.spinnerReplacementReason.setSpinnerTextFormatter(textFormatter1)
            binding.spinnerReplacementReason.setSelectedTextFormatter(textFormatter1)

            binding.spinnerReplacementReason.onSpinnerItemSelectedListener =
                OnSpinnerItemSelectedListener { parent, view, position, id ->
                    val item = binding.spinnerReplacementReason.selectedItem as ReplacementReasonItem
                    viewModel.replacementReasonId = item.replacementReasonID

                }
            binding.spinnerReplacementReason.attachDataSource(replacementData)
        }

    }

    private fun bindSymptomsSpinner(replacementData: MutableList<ProductSymptomsItem?>?) {

        replacementData?.let {
            val textFormatter1 =
                SpinnerTextFormatter<ProductSymptomsItem> { obj ->
                    SpannableString(
                        obj.symptoms
                    )
                }
            binding.layoutComplainInfo.spinnerSymptoms.setSpinnerTextFormatter(textFormatter1)
            binding.layoutComplainInfo.spinnerSymptoms.setSelectedTextFormatter(textFormatter1)

            binding.layoutComplainInfo.spinnerSymptoms.onSpinnerItemSelectedListener =
                OnSpinnerItemSelectedListener { parent, view, position, id ->
                    val item = binding.layoutComplainInfo.spinnerSymptoms.selectedItem as ProductSymptomsItem
                    viewModel.symptomsId = item.symptomID
                    binding.layoutComplainInfo.llDefectReason.visibility = View.VISIBLE
                    binding.layoutComplainInfo.llRepairActions.visibility = View.GONE
                    binding.layoutComplainInfo.llRepairType.visibility = View.GONE

                    viewModel.defectReasonId = 0
                    viewModel.repairActionId = 0
                    viewModel.repairTypeId = 0

                    viewModel.getDefectReasonList(modelItem?.DivisionID, modelItem?.CategoryID, viewModel.symptomsId)
                }
            binding.layoutComplainInfo.spinnerSymptoms.attachDataSource(replacementData)
        }

    }

    private fun bindDefectReasonSpinner(defectReasonData: MutableList<DefectReasonItem?>?) {

        defectReasonData?.let {
            val textFormatter1 =
                SpinnerTextFormatter<DefectReasonItem> { obj ->
                    SpannableString(
                        obj.defectReason
                    )
                }
            binding.layoutComplainInfo.spinnerDefectReasons.setSpinnerTextFormatter(textFormatter1)
            binding.layoutComplainInfo.spinnerDefectReasons.setSelectedTextFormatter(textFormatter1)

            binding.layoutComplainInfo.spinnerDefectReasons.onSpinnerItemSelectedListener =
                OnSpinnerItemSelectedListener { parent, view, position, id ->
                    val item = binding.layoutComplainInfo.spinnerDefectReasons.selectedItem as DefectReasonItem
                    viewModel.defectReasonId = item.defectReasonID

                    binding.layoutComplainInfo.llRepairActions.visibility = View.VISIBLE
                    binding.layoutComplainInfo.llRepairType.visibility = View.GONE

                    viewModel.repairActionId = 0
                    viewModel.repairTypeId = 0

                    viewModel.getRepairActionDetailsList(modelItem?.DivisionID, modelItem?.CategoryID, viewModel.symptomsId, viewModel.defectReasonId)
                }
            binding.layoutComplainInfo.spinnerDefectReasons.attachDataSource(defectReasonData)
        }

    }

    private fun bindRepairActionDetailSpinner(repairActionData: MutableList<RepairActionDetailItem?>?) {

        repairActionData?.let {
            val textFormatter1 =
                SpinnerTextFormatter<RepairActionDetailItem> { obj ->
                    SpannableString(
                        obj.repairAction
                    )
                }
            binding.layoutComplainInfo.spinnerRepairAction.setSpinnerTextFormatter(textFormatter1)
            binding.layoutComplainInfo.spinnerRepairAction.setSelectedTextFormatter(textFormatter1)

            binding.layoutComplainInfo.spinnerRepairAction.onSpinnerItemSelectedListener =
                OnSpinnerItemSelectedListener { parent, view, position, id ->
                    val item = binding.layoutComplainInfo.spinnerRepairAction.selectedItem as RepairActionDetailItem
                    viewModel.repairActionId = item.repairActionID

                    binding.layoutComplainInfo.llRepairType.visibility = View.VISIBLE
                    viewModel.repairTypeId = 0

                    viewModel.getRepairTypeList(modelItem?.TicketID)
                }
            binding.layoutComplainInfo.spinnerRepairAction.attachDataSource(repairActionData)
        }

    }

    private fun bindRepairTypeSpinner(repairTypeData: MutableList<RepairTypeItem?>?) {

        repairTypeData?.let {
            val textFormatter1 =
                SpinnerTextFormatter<RepairTypeItem> { obj ->
                    SpannableString(
                        obj.repairType
                    )
                }
            binding.layoutComplainInfo.spinnerRepairType.setSpinnerTextFormatter(textFormatter1)
            binding.layoutComplainInfo.spinnerRepairType.setSelectedTextFormatter(textFormatter1)

            binding.layoutComplainInfo.spinnerRepairType.onSpinnerItemSelectedListener =
                OnSpinnerItemSelectedListener { parent, view, position, id ->
                    val item = binding.layoutComplainInfo.spinnerRepairType.selectedItem as RepairTypeItem
                    viewModel.repairTypeId = item.repairTypeID

                }
            binding.layoutComplainInfo.spinnerRepairType.attachDataSource(repairTypeData)
        }

    }


    private fun clickListeners() {


        binding.layoutProductInfo.radioGroupRepair.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { radioGroup, i ->

            if (binding.layoutProductInfo.radioButtonYesRepair.isChecked()) {
                isNoRepair = false
                binding.layoutProductInfo.llGenerateInvoiceView.visibility = View.VISIBLE
            }

            if (binding.layoutProductInfo.radioButtonNoRepair.isChecked()) {
                isNoRepair = true
                binding.layoutProductInfo.llGenerateInvoiceView.visibility = View.GONE
            }

        })


        binding.layoutProductInfo.radioGroupReplacementReqd.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { radioGroup, i ->

            if (binding.layoutProductInfo.radioButtonNoReplacement.isChecked()) {
                isReplacementRequired = false
                binding.layoutProductInfo.llUploadReplacementQrCode.visibility = View.GONE
            }

            if (binding.layoutProductInfo.radioButtonYesReplacement.isChecked()) {
                isReplacementRequired = true
                binding.layoutProductInfo.llUploadReplacementQrCode.visibility = View.VISIBLE
            }

        })


        binding.layoutProductInfo.radioGroupGenerateInvAgain.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { radioGroup, i ->

            if (binding.layoutProductInfo.radioBtnNoGenerateAgain.isChecked()) {
                binding.layoutProductInfo.btnGenerateInvoice.visibility = View.GONE
                binding.layoutProductInfo.txtPdf.visibility = View.VISIBLE
                binding.btnScanQr.visibility = View.GONE
                binding.layoutProductInfo.tvScanAgain.visibility = View.GONE
            }

            if (binding.layoutProductInfo.radioBtnYesGenerateAgain.isChecked()) {
                binding.layoutProductInfo.btnGenerateInvoice.visibility = View.VISIBLE
                binding.layoutProductInfo.txtPdf.visibility = View.GONE
                binding.btnScanQr.visibility = View.VISIBLE
                binding.layoutProductInfo.tvScanAgain.visibility = View.VISIBLE
            }

        })


        binding.layoutProductInfo.txtPdf.setOnClickListener {
            if ((modelItem?.InvoicePDF ?: "").isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "You can view generated Invoice from Invoice history",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                WebActivity.start(requireContext(), modelItem?.InvoicePDF ?: "")
            }

        }

        binding.layoutProductInfoHeader.itemExpandableHeaderRoot.setOnClickListener {
            toggleProductInfo()
        }
        binding.layoutComplainInfoHeader.itemExpandableHeaderRoot.setOnClickListener {
            toggleComplainInfo()
        }
        binding.layoutWiringDevicesHeader.itemExpandableHeaderRoot.setOnClickListener {
            context?.let { it1 ->
                modelItem?.TicketID?.let { ticketId ->
                    WiringDeviceFormActivity.start(it1, ticketId)
                }
            }
        }
        binding.layoutAccountsInfoHeader.itemExpandableHeaderRoot.setOnClickListener {
            toggleAccountsInfo()
        }



        binding.btnScanQr.setOnClickListener {

            childFragmentManager.let {
                OptionsBottomSheetFragment.newInstance(Bundle()).apply {
                    show(it, tag)
                }
            }
        }

// - -  - - for bill upload
        binding.layoutProductInfo.btnAddBill.setOnClickListener {
            openDocumentsDialog(requireContext(), this)
            uploadCallFrom = 1
        }

        binding.layoutProductInfo.iconRemoveBill.setOnClickListener {
            binding.layoutProductInfo.iconBill.setImageDrawable(null)
            binding.layoutProductInfo.widgetBill.visibility = View.GONE
            binding.layoutProductInfo.textViewBillTitle.text = ""
            viewModel.strBillImage = ""
            binding.rootLayout.snackbar("Bill removed successfully")
        }


        // - -  - - for product upload
        binding.layoutProductInfo.btnAddProduct.setOnClickListener {
            openDocumentsDialog(requireContext(), this)
            uploadCallFrom = 2
        }

        binding.layoutProductInfo.iconRemoveProduct.setOnClickListener {
            binding.layoutProductInfo.iconProduct.setImageDrawable(null)
            binding.layoutProductInfo.widgetProduct.visibility = View.GONE
            binding.layoutProductInfo.textViewProductTitle.text = ""
            viewModel.strProductImage = "-"
            binding.rootLayout.snackbar("Product removed successfully")
        }

        // - -  - - for qrcode upload
        binding.layoutProductInfo.btnAddQrCode.setOnClickListener {
            openDocumentsDialog(requireContext(), this)
            uploadCallFrom = 3
        }

        binding.layoutProductInfo.iconRemoveQrCode.setOnClickListener {
            binding.layoutProductInfo.iconQrCode.setImageDrawable(null)
            binding.layoutProductInfo.widgetQrCode.visibility = View.GONE
            binding.layoutProductInfo.textViewQrCodeTitle.text = ""
            viewModel.strQRImage = "-"
            binding.rootLayout.snackbar("QrCode removed successfully")
        }


        // - -  - - for selfie upload
        binding.layoutComplainInfo.btnAddSelfie.setOnClickListener {
            openDocumentsDialog(requireContext(), this)
            uploadCallFrom = 4
        }

        binding.layoutComplainInfo.iconRemoveSelfie.setOnClickListener {
            binding.layoutComplainInfo.iconSelfie.setImageDrawable(null)
            binding.layoutComplainInfo.widgetSelfie.visibility = View.GONE
            binding.layoutComplainInfo.textViewSelfieTitle.text = ""
            viewModel.strSelfieImage = "-"
            binding.rootLayout.snackbar("Selfie removed successfully")
        }


        // - -  - - for replacement upload
        binding.layoutProductInfo.btnAddReplacementQrCode.setOnClickListener {
            QrCodeScanActivity.start(requireContext(), this,"replacement")
        }

        /*binding.layoutComplainInfo.btnWiringForm.setOnClickListener {
            context?.let { it1 ->
                modelItem?.TicketID?.let { ticketId ->
                    WiringDeviceFormActivity.start(it1, ticketId)
                }
            }
        }*/


        binding.layoutProductInfo.txtDOP.setOnClickListener {
            val c = Calendar.getInstance()
            val mYear = c[Calendar.YEAR]
            val mMonth = c[Calendar.MONTH]
            val mDay = c[Calendar.DAY_OF_MONTH]

            val endDatePicker = DatePickerDialog(
                requireContext(), R.style.SpinnerDatePickerStyle,
                { view, year, monthOfYear, dayOfMonth ->
                    binding.layoutProductInfo.txtDOP.text = String.format(
                        Locale.getDefault(),
                        "%d/%d/%d",
                        dayOfMonth,
                        monthOfYear + 1,
                        year
                    )
                    viewModel.strDateOfPurchase =
                        (monthOfYear + 1).toString() + "-" + dayOfMonth + "-" + year
                }, mYear, mMonth, mDay
            )
            endDatePicker.datePicker.maxDate = c.timeInMillis
            endDatePicker.show()
        }

        binding.layoutReschedule.txtAppointmentDate.setOnClickListener {


            //start after 1 day from now

            val startDate = Calendar.getInstance()
            startDate.add(Calendar.DAY_OF_MONTH, 1)

            val mYear = startDate[Calendar.YEAR]
            val mMonth = startDate[Calendar.MONTH]
            val mDay = startDate[Calendar.DAY_OF_MONTH]


            //end after 1 week from now
            val endDate = Calendar.getInstance()
            endDate.add(Calendar.DAY_OF_MONTH, 7)

            val endDatePicker = DatePickerDialog(
                requireContext(),
                { view, year, monthOfYear, dayOfMonth ->
                    binding.layoutReschedule.txtAppointmentDate.text = String.format(
                        Locale.getDefault(),
                        "%d/%d/%d",
                        dayOfMonth,
                        monthOfYear + 1,
                        year
                    )
                    viewModel.strRescheduleDate =
                        (monthOfYear + 1).toString() + "-" + dayOfMonth + "-" + year
                }, mYear, mMonth, mDay
            )
            endDatePicker.datePicker.minDate = startDate.timeInMillis
            endDatePicker.datePicker.maxDate = endDate.timeInMillis
            endDatePicker.show()
        }


        binding.layoutProductInfo.btnGenerateInvoice.setOnClickListener {
            //GenerateInvoiceActivity.start(requireContext())

            val intent = Intent(requireContext(), GenerateInvoiceActivity::class.java)
            GenerateInvoiceActivity.refreshListener = this
            intent.putExtra(ARG_PARAM, modelItem)
            //  Toast.makeText(requireContext(), "QrCode  ScanType- - -"+qrCode+" - - -"+passSearchType.toString(), Toast.LENGTH_SHORT).show()
            intent.putExtra("ScanType", passSearchType)
            intent.putExtra("QrCode", qrCode)
            startActivityForResult(intent, REFRESH_RESULT_CODE)
        }

        binding.layoutProductInfo.btnAddParts.setOnClickListener {
            PartsRequirementActivity.start(requireContext(), "Ticket", modelItem!!)
        }


        binding.layoutProductInfo.imvProduct?.setOnClickListener {
            if (!modelItem?.ProductImage.isNullOrEmpty()) {
                val intent = Intent(requireContext(), FullscreenImageActivity::class.java)
                    .putExtra(ARG_IMAGE_URL, modelItem?.InvoicePDF)
                startActivity(intent)
            }
        }

        binding.layoutProductInfo.imvProduct?.setOnClickListener {
            downloadFile(
                requireContext(),
                modelItem?.ProductImage ?: "",
                (modelItem?.InvoicePDF ?: "")
            )
        }


//  - - - - - - click on update status - -  - - - - - -
        binding.btnUpdateStatus.setOnClickListener {
                closeTicketApi()
        }
    }


    fun closeTicketApi(){
        // - - - -  In case of close ticket show popup to verify if its from same location or not
        viewModel.strSerialNo = binding.layoutProductInfo.edtSlNo.text.toString()
        viewModel.strSerialNoRemark = binding.layoutProductInfo.edtEanRemark.text.toString()
        viewModel.inWarranty = binding.layoutProductInfo.radioButtonYes.isChecked


        viewModel.strUpdateStatusRemarks =
            binding.layoutEngineerRemark.edtEngineerRemark.text.toString()

        Wherebout(requireContext()).onChange(object : Workable<GPSPoint?> {
            override fun work(t: GPSPoint?) {
                // draw something in the UI with this new data
                latitude = t?.latitude
                longitude = t?.longitude


                // - - - - verify distance between checkin and checkout - - - - -
                if (viewModel.actionId == 3) {
                    val results1 = FloatArray(1)
                    Location.distanceBetween(
                        latitude ?: 0.0,
                        longitude ?: 0.0,
                        (modelItem?.CheckinLat ?: "0.0").toDouble(),
                        (modelItem?.CheckinLong ?: "0.0").toDouble(),
                        results1
                    )
                    strCheckoutDistance = results1[0].toString()
                }


                viewModel.getLoggedInUser().observe(viewLifecycleOwner, Observer { user ->
                    if (user != null) {
                        viewModel.getOtpForCloseTicket(
                            strMobileNo = modelItem?.CustContactNo ?: "-",
                            intTicketId = modelItem?.TicketID ?: -1,
                            strTicketNo = modelItem?.TicketNo ?: "-",
                            intUserId = user.UserId,
                            strCustName = modelItem?.CustName ?: "-",
                            latitude = latitude.toString(),
                            longitude = longitude.toString(),
                            location = getAddressFromLatLong(
                                requireContext(),
                                latitude ?: 0.0,
                                longitude ?: 0.0
                            )
                                ?: "Unnamed Road",
                            isOutOfPremises = outsidePremises ?: false,
                            strOutOfPremisesRemark = strPremisesRemark ?: "-",
                            strCheckoutDistance = strCheckoutDistance ?: "0.0",
                            purchaseDate = modelItem?.IsDealerCall ?: false,
                            isNoRepair = isNoRepair,
                            isProductReplaced = isReplacementRequired,
                            intResendOtp = resendOtp,
                            strDeviceId = strDeviceId
                        )

                    }
                })

            }
        })
    }


    // - - - - Alert to verify customer location - - - - - -
    fun verifyCustomerLocation() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("CHECK IF IN PREMISES ?")
        builder.setMessage("Please select 'YES' if you are closing ticket within the customer premises and 'NO' if outside the customer premises with valid remark.")

        val input = EditText(requireContext())
        input.hint = "Enter Remark"
        input.inputType =
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
        builder.setView(input)

        builder.setPositiveButton(R.string.str_yes) { dialog, which ->
            strPremisesRemark = "-"
            outsidePremises = false
            updateTicketAPI()
            dialog.cancel()
        }

        builder.setNegativeButton(R.string.str_no) { dialog, which ->
            val strInput = input.text?.toString()
            strInput?.let { Log.d("Input textfield ** **", it) }
            if (strInput.isNullOrEmpty() || strInput.equals("-")) {
                binding.rootLayout.snackbar("Please Enter Valid Remark")
                return@setNegativeButton
            }
            strPremisesRemark = strInput
            outsidePremises = true
            updateTicketAPI()
        }

        val dialog = builder.create()
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        dialog.show()
    }


    fun updateTicketAPI() {
        viewModel.getLoggedInUser().observe(viewLifecycleOwner, Observer { user ->
            if (user != null) {
                viewModel.updateTicketStatus(
                    userId = user.UserId,
                    ticketId = modelItem?.TicketID ?: -1,
                    latitude = latitude.toString(),
                    longitude = longitude.toString(),
                    location = getAddressFromLatLong(
                        requireContext(),
                        latitude ?: 0.0,
                        longitude ?: 0.0
                    ) ?: "Unnamed Road",
                    outOfPremises = outsidePremises ?: false,
                    strOutOfPremisesRemark = strPremisesRemark ?: "-",
                    strCheckoutDistance = strCheckoutDistance ?: "0.0",
                    isNoRepair = isNoRepair,
                    isProductReplaced = isReplacementRequired
                )
            }
        })
    }


    private fun toggleProductInfo() {

        if (binding.layoutProductInfo.productRoot.visibility == View.VISIBLE) {
            binding.layoutProductInfo.productRoot.visibility = View.GONE
            binding.layoutProductInfoHeader.itemExpandableHeaderIcon.setImageResource(R.drawable.ic_plus)
        } else {
            binding.layoutProductInfo.productRoot.visibility = View.VISIBLE
            binding.layoutProductInfoHeader.itemExpandableHeaderIcon.setImageResource(R.drawable.ic_minus)
        }
        //Hide other views
        binding.layoutComplainInfo.complainRoot.visibility = View.GONE
        binding.layoutComplainInfoHeader.itemExpandableHeaderIcon.setImageResource(R.drawable.ic_plus)
        binding.layoutAccountsInfo.complainRoot.visibility = View.GONE
        binding.layoutAccountsInfoHeader.itemExpandableHeaderIcon.setImageResource(R.drawable.ic_plus)
    }


    private fun openProductInfo() {

        binding.layoutProductInfo.productRoot.visibility = View.VISIBLE
//        if (binding.layoutProductInfo.productRoot.visibility == View.VISIBLE) {
//            binding.layoutProductInfo.productRoot.visibility = View.GONE
        binding.layoutProductInfoHeader.itemExpandableHeaderIcon.setImageResource(R.drawable.ic_minus)
//        } else {
//            binding.layoutProductInfoHeader.itemExpandableHeaderIcon.setImageResource(R.drawable.ic_minus)
//        }
        //Hide other views
        binding.layoutComplainInfo.complainRoot.visibility = View.GONE
        binding.layoutComplainInfoHeader.itemExpandableHeaderIcon.setImageResource(R.drawable.ic_plus)
        binding.layoutAccountsInfo.complainRoot.visibility = View.GONE
        binding.layoutAccountsInfoHeader.itemExpandableHeaderIcon.setImageResource(R.drawable.ic_plus)
    }

    private fun toggleAccountsInfo() {
        if (binding.layoutAccountsInfo.complainRoot.visibility == View.VISIBLE) {
            binding.layoutAccountsInfo.complainRoot.visibility = View.GONE
            binding.layoutAccountsInfoHeader.itemExpandableHeaderIcon.setImageResource(R.drawable.ic_plus)
        } else {
            binding.layoutAccountsInfo.complainRoot.visibility = View.VISIBLE
            binding.layoutAccountsInfoHeader.itemExpandableHeaderIcon.setImageResource(R.drawable.ic_minus)
        }
        //Hide other views
        binding.layoutProductInfo.productRoot.visibility = View.GONE
        binding.layoutProductInfoHeader.itemExpandableHeaderIcon.setImageResource(R.drawable.ic_plus)
        binding.layoutComplainInfo.complainRoot.visibility = View.GONE
        binding.layoutComplainInfoHeader.itemExpandableHeaderIcon.setImageResource(R.drawable.ic_plus)
    }


    private fun toggleComplainInfo() {
        if (binding.layoutComplainInfo.complainRoot.visibility == View.VISIBLE) {
            binding.layoutComplainInfo.complainRoot.visibility = View.GONE
            binding.layoutComplainInfoHeader.itemExpandableHeaderIcon.setImageResource(R.drawable.ic_plus)
        } else {
            binding.layoutComplainInfo.complainRoot.visibility = View.VISIBLE
            binding.layoutComplainInfoHeader.itemExpandableHeaderIcon.setImageResource(R.drawable.ic_minus)
        }
        //Hide other views
        binding.layoutProductInfo.productRoot.visibility = View.GONE
        binding.layoutProductInfoHeader.itemExpandableHeaderIcon.setImageResource(R.drawable.ic_plus)
        binding.layoutAccountsInfo.complainRoot.visibility = View.GONE
        binding.layoutAccountsInfoHeader.itemExpandableHeaderIcon.setImageResource(R.drawable.ic_plus)
    }


    private fun setCategoryChips(categorys: List<SymptomsData?>?) {

        categorys?.let {

            binding.layoutComplainInfo.chipGrp.removeAllViews()
            binding.layoutComplainInfo.chipGrp.visibility = View.VISIBLE
            for (category in categorys) {
                val mChip =
                    this.layoutInflater.inflate(R.layout.item_chip_symptoms, null, false) as Chip
                mChip.text = category?.SymptomsName
                mChip.id = category?.Slno ?: -1
                val paddingDp = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 10f,
                    resources.displayMetrics
                ).toInt()
                mChip.setPadding(paddingDp, 0, paddingDp, 0)
                mChip.setOnCheckedChangeListener { chip, isChecked ->

                    Log.d(TAG, "setCategoryChips: " + chip.id)
                    val checkedChipIds = binding.layoutComplainInfo.chipGrp.checkedChipIds


                    viewModel.strProductSymptoms = checkedChipIds.joinToString()

                    Log.d(TAG, "- - - -- - - - - - ")
                    Log.d(TAG, "checked ids: " + checkedChipIds)
                    Log.d(TAG, "comma sepeated: " + viewModel.strProductSymptoms)
                }
                binding.layoutComplainInfo.chipGrp.addView(mChip)
            }
        }

    }


    private fun searchQRCode(type: Int) {
        val builder = AlertDialog.Builder(requireContext())

        var msg = ""
        var errorMsg = ""

        if (type == 1) {
            msg = "Enter QR Code"
            errorMsg = "Please Enter QR Code"
        } else if (type == 2) {
            msg = "Enter EAN No"
            errorMsg = "Please Enter EAN No"
        } else if (type == 3) {
            msg = "Enter Product Code"
            errorMsg = "Please Enter Product Code"
        }

        builder.setTitle(msg)

// Set up the input
        val input = EditText(requireContext())

// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.inputType =
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS

        builder.setView(input)
// Set up the buttons
        builder.setPositiveButton(R.string.str_ok) { dialog, which ->
            strInput = input.text.toString()

            if (strInput.isNullOrEmpty()) {
                binding.rootLayout.snackbar(errorMsg)
                return@setPositiveButton
            }
            viewModel.strQrCode = strInput
            if (modelItem?.CustomerID ?: 0 == 0) {
                Toast.makeText(requireContext(), "Invalid Customer", Toast.LENGTH_SHORT).show()
            } else {
                intSearchType = type
                viewModel.searchQrCode(
                    strInput,
                    modelItem?.CustomerID ?: 0,
                    intSearchType,
                    modelItem?.TicketID ?: 0,
                    false
                )
            }

        }
        builder.setNegativeButton(R.string.str_cancel) { dialog, which ->
            dialog.cancel()
        }

        val dialog = builder.create()
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        input.requestFocus()
        dialog.show()
    }


    private fun showSuccessAlert(actionStatus: Int?) {

        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        var statusMessage = ""
        when (actionStatus) {

            //Re-Assign
            1 -> {
                statusMessage = "Re-Assign"
            }
            //Re Schedule
            2 -> {
                statusMessage = "Re-Schedule"
            }
            //Close
            3 -> {
                statusMessage = "Closed"
            }
        }
        val message =
            "Ticket no ${modelItem?.TicketNo} status updated as $statusMessage successfully!"
        builder.setMessage(message)
        builder.setPositiveButton(R.string.str_ok, object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface, id12: Int) {
                requireActivity().setResult(REFRESH_RESULT_CODE)
                requireActivity().finish()
            }
        })
        val alertDialog = builder.create()
        alertDialog.show()

    }


    @AfterPermissionGranted(RC_CAMERA_PERM)
    private fun onClickRequestPermissionCameraButton() {
        if (EasyPermissions.hasPermissions(context, Manifest.permission.CAMERA)) {
            // Have permission, do the thing!

            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, RC_CAMERA_PERM)
        } else {
            // Request one permission
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.permission_camera_rationale_message),
                RC_CAMERA_PERM,
                Manifest.permission.CAMERA
            )
        }
    }


    @AfterPermissionGranted(RC_STORAGE_PERM)
    private fun onClickRequestPermissionStorageButton() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            if (EasyPermissions.hasPermissions(
                    context,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                // Have permission, do the thing!
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.type = "*/*"
                val mimetypes = arrayOf("image/*"/*, "application/pdf"*/)
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes)
                startActivityForResult(intent, RC_STORAGE_PERM)
            } else {
                // Request one permission
                EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.permission_storage_rationale_message),
                    RC_STORAGE_PERM,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            }
        } else {
            if (EasyPermissions.hasPermissions(
                    context,
                    Manifest.permission.READ_MEDIA_IMAGES
                )
            ) {
                // Have permission, do the thing!
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.type = "*/*"
                val mimetypes = arrayOf("image/*"/*, "application/pdf"*/)
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes)
                startActivityForResult(intent, RC_STORAGE_PERM)
            } else {
                // Request one permission
                EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.permission_storage_rationale_message),
                    RC_STORAGE_PERM,
                    Manifest.permission.READ_MEDIA_IMAGES
                )
            }
        }

    }


    private fun prepareImgUpload(thumbnail: Bitmap) {
        var rlLayout: RelativeLayout? = null
        var icon: CircleImageView? = null

        val scaledBitmap = scaleDown(thumbnail, FULL_IMAGE_SIZE, true)

        when (uploadCallFrom) {
            1 -> {
                viewModel.strBillImage = convertBitmapToBase64(scaledBitmap)
                rlLayout = binding.layoutProductInfo.widgetBill
                icon = binding.layoutProductInfo.iconBill
            }
            2 -> {
                viewModel.strProductImage = convertBitmapToBase64(scaledBitmap)
                rlLayout = binding.layoutProductInfo.widgetProduct
                icon = binding.layoutProductInfo.iconProduct
            }

            3 -> {
                viewModel.strQRImage = convertBitmapToBase64(scaledBitmap)
                rlLayout = binding.layoutProductInfo.widgetQrCode
                icon = binding.layoutProductInfo.iconQrCode
            }
            4 -> {
                viewModel.strSelfieImage = convertBitmapToBase64(scaledBitmap)
                rlLayout = binding.layoutComplainInfo.widgetSelfie
                icon = binding.layoutComplainInfo.iconSelfie
            }

            else -> println("Not matching upload number")
        }


        val displayedBitmap = scaleDown(thumbnail, THUMBNAIL_SIZE, true)

        rlLayout?.visibility = View.VISIBLE
        icon?.setImageBitmap(displayedBitmap)

        binding.rootLayout.snackbar("Image attached successfully")

    }


    private fun prepareDocumentUpload(filePath: String) {
        binding.layoutProductInfo.widgetBill.visibility = View.VISIBLE
        val thumbnail = getBitmap(filePath)
        if (thumbnail != null) {
            val scaledBitmap = scaleDown(thumbnail, FULL_IMAGE_SIZE, true)
            viewModel.strBillImage = convertBitmapToBase64(scaledBitmap)
            val displayedBitmap = scaleDown(thumbnail, THUMBNAIL_SIZE, true)
            binding.layoutProductInfo.iconBill.setImageBitmap(displayedBitmap)
        } else {
            viewModel.strBillImage = convertDocumentToBase64(filePath)
            binding.layoutProductInfo.iconBill.setImageDrawable(
                AppCompatResources.getDrawable(requireContext(), R.drawable.ic_outline_pdf)
            )
        }

        Log.d(TAG, "prepareDocumentUpload: " + viewModel.strBillImage)


        binding.layoutProductInfo.textViewBillTitle.text =
            filePath.substring(filePath.lastIndexOf("/") + 1)
        binding.rootLayout.snackbar("Bill attached successfully")
    }


    override fun onOtpReceived(otp: String) {

// - - - - Reject otp  - - - - -

        viewModel.serviceOTP = otp
        //updateTicketAPI()
        verifyCustomerLocation()

    }


    override fun onResendOtp(resend: Int) {
        Log.d(TAG, "onResendOtp: ")
        resendOtp = resend
       closeTicketApi()
    }


    companion object {

        private const val RC_CAMERA_PERM = 122
        private const val RC_STORAGE_PERM = 123
        private const val REFRESH_RESULT_CODE = 102
        private const val RC_LOCATION_PERM = 121
        private const val ARG_IMAGE_URL = "image_url"


        @JvmStatic
        fun newInstance(modelItem: GetTicketDetailsData?) =
            ComplainTabFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PARAM, modelItem)
                }
            }

    }

    override fun onRefresh() {
        invGenerated = true
        binding.layoutProductInfo.llNoRepairMain.visibility = View.GONE
        binding.layoutProductInfo.btnGenerateInvoice.visibility = View.GONE
        binding.layoutProductInfo.txtPdf.visibility = View.VISIBLE
        binding.btnScanQr.visibility = View.VISIBLE

        binding.layoutProductInfo.radioBtnNoGenerateAgain.isChecked = true
        binding.layoutProductInfo.radioBtnYesGenerateAgain.isChecked = false

        binding.layoutProductInfo.tvScanAgain.visibility = View.GONE
        binding.layoutProductInfo.llGenerateInvoiceAgainView.visibility = View.VISIBLE

    }
}
