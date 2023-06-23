package com.goldmedal.crm.ui.ticket

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.lifecycle.ViewModelProvider
import com.goldmedal.crm.R
import com.goldmedal.crm.common.ApiStageListener
import com.goldmedal.crm.data.model.wiringDeviceForm.*
import com.goldmedal.crm.data.network.GlobalConstant
import com.goldmedal.crm.databinding.ActivityWiringDeviceFormBinding
import com.goldmedal.crm.databinding.WiringDevicePointDialogBinding
import com.goldmedal.crm.util.snackbar
import com.goldmedal.crm.util.toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import kotlinx.android.synthetic.main.item_wiring_device_multi_selection.view.*
import org.json.JSONArray
import org.json.JSONObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

enum class PointTableType {
    WATTAGE_L1,
    WATTAGE_L2,
    WATTAGE_L3,
    WATTAGE_L4,
    PF_L1,
    PF_L2,
    PF_L3,
    PF_L4,
    CURRENT_L1,
    CURRENT_L2,
    CURRENT_L3,
    CURRENT_L4
}

const val TICKET_ID = "ticket_id"

class WiringDeviceFormActivity : AppCompatActivity(), KodeinAware, ApiStageListener<Any> {
    override val kodein by kodein()
    private val TAG = WiringDeviceFormActivity::class.java.simpleName
    private var _binding: ActivityWiringDeviceFormBinding? = null
    private val binding get() = _binding!!
    private val factory: TicketViewModelFactory by instance()
    private lateinit var viewModel: TicketViewModel
    private var phaseList = ArrayList<Phase>()
    private var supplyList = ArrayList<Supply>()
    private var voltageViewCount = 0
    private var channelViewCount = 0
    private var loadDescriptionViewCount = 0
    private var typeLedViewCount = 0
    private var voltageList = ArrayList<Voltage>()
    private var faultyChannelList = ArrayList<FaultyChannel>()
    private var loadDescriptionList = ArrayList<LoadDescription>()
    private var typeLedList = ArrayList<TypeLED>()
    private var mTicketId = 0
    private var isClearAll = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent?.let {
            mTicketId = intent.getIntExtra(TICKET_ID, 0)
        }
        _binding = ActivityWiringDeviceFormBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
        setClickListeners()
    }

    private fun initViews() {
        viewModel = ViewModelProvider(this, factory).get(TicketViewModel::class.java)
        viewModel.apiListener = this
        viewModel.getWiringDeviceFormData(mTicketId)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setClickListeners() {
        // Point table click listeners
        binding.tvWattageL1.setOnClickListener { showPointsInputDialog(PointTableType.WATTAGE_L1) }
        binding.tvWattageL2.setOnClickListener { showPointsInputDialog(PointTableType.WATTAGE_L2) }
        binding.tvWattageL3.setOnClickListener { showPointsInputDialog(PointTableType.WATTAGE_L3) }
        binding.tvWattageL4.setOnClickListener { showPointsInputDialog(PointTableType.WATTAGE_L4) }
        binding.tvPfL1.setOnClickListener { showPointsInputDialog(PointTableType.PF_L1) }
        binding.tvPfL2.setOnClickListener { showPointsInputDialog(PointTableType.PF_L2) }
        binding.tvPfL3.setOnClickListener { showPointsInputDialog(PointTableType.PF_L3) }
        binding.tvPfL4.setOnClickListener { showPointsInputDialog(PointTableType.PF_L4) }
        binding.tvCurrentL1.setOnClickListener { showPointsInputDialog(PointTableType.CURRENT_L1) }
        binding.tvCurrentL2.setOnClickListener { showPointsInputDialog(PointTableType.CURRENT_L2) }
        binding.tvCurrentL3.setOnClickListener { showPointsInputDialog(PointTableType.CURRENT_L3) }
        binding.tvCurrentL4.setOnClickListener { showPointsInputDialog(PointTableType.CURRENT_L4) }

        binding.viewVoltageAdd.setOnClickListener {
            if (voltageList.isNotEmpty() && voltageViewCount < voltageList.size) {
                addVoltageView(voltageList, true)
            }
        }
        binding.viewFaultyChannelAdd.setOnClickListener {
            if (faultyChannelList.isNotEmpty() && channelViewCount < faultyChannelList.size) {
                addFaultyChannelView(faultyChannelList, true)
            }
        }
        binding.viewLoadDescriptionAdd.setOnClickListener {
            if (loadDescriptionList.isNotEmpty() && loadDescriptionViewCount < loadDescriptionList.size) {
                addLoadDescriptionView(loadDescriptionList, true)
            }
        }
        binding.viewTypeLedAdd.setOnClickListener {
            if (typeLedList.isNotEmpty() && typeLedViewCount < typeLedList.size) {
                addTypeLedView(typeLedList, true)
            }
        }

        binding.tvVoltageClearAll.setOnClickListener {
            voltageViewCount = 0
            binding.llVoltage.removeAllViews()
            addVoltageView(voltageList, true)
        }
        binding.tvFaultyChannelClearAll.setOnClickListener {
            channelViewCount = 0
            binding.llFaultyChannel.removeAllViews()
            addFaultyChannelView(faultyChannelList, true)
        }
        binding.tvLoadDescriptionClearAll.setOnClickListener {
            loadDescriptionViewCount = 0
            binding.llLoadDescription.removeAllViews()
            addLoadDescriptionView(loadDescriptionList, true)
        }
        binding.tvTypeLedClearAll.setOnClickListener {
            typeLedViewCount = 0
            binding.llTypeLed.removeAllViews()
            addTypeLedView(typeLedList, true)
        }
        binding.btnSubmit.setOnClickListener {
            prepareDataAndCallApi()
        }
    }

    private fun prepareDataAndCallApi() {
        try {
            // Id mapping: 1 - Single (Phase), 2 - Three (Phase), 3 - Normal (Supply), 4 - Inverter (Supply), 5 - Generator (Supply
            val phaseListId = mutableListOf<String>()
            if (binding.cbPhaseSingle.isChecked) {
                phaseListId.add("1")
            }
            if (binding.cbPhaseThree.isChecked) {
                phaseListId.add("2")
            }
            val phaseIds = phaseListId.joinToString { it }
            Log.d(TAG, phaseIds.filter { !it.isWhitespace() })
            if (phaseListId.isEmpty()) {
                Toast.makeText(this, "Select Phase Item", Toast.LENGTH_SHORT).show()
                return
            }

            val supplyListId = mutableListOf<String>()
            if (binding.cbSupplyNormal.isChecked) {
                supplyListId.add("3")
            }
            if (binding.cbSupplyInverter.isChecked) {
                supplyListId.add("4")
            }
            if (binding.cbSupplyGenerator.isChecked) {
                supplyListId.add("5")
            }
            val supplyIds = supplyListId.joinToString { it }
            Log.d(TAG, supplyIds.filter { !it.isWhitespace() })
            if (supplyListId.isEmpty()) {
                Toast.makeText(this, "Select Supply Item", Toast.LENGTH_SHORT).show()
                return
            }

            // convert voltage list to jsonArray
            val voltageJsonArray = getJsonArrayForVoltage()
            val channelJsonArray = getJsonArrayFaultyChannel()
            val loadDescriptionJsonArray = getJsonArrayLoadDescription()
            val typeLedJsonArray = getJsonArrayTypeLed()

            val powerFactor = binding.tietPowerFactor.text.toString()
            val shortRemark = binding.tietDetails.text.toString()
            val brandName = binding.tietMake.text.toString()
            if (brandName.isEmpty()) {
                toast("Please enter make remarks.")
                return
            }

            val faultyChannelDetailsJsonObject = getFaultyChannelDetailsJsonObject()
            val l1Wattage = binding.tvWattageL1.text.toString()
            val l1PF = binding.tvPfL1.text.toString()
            val l1Current = binding.tvCurrentL1.text.toString()

            val l2Wattage = binding.tvWattageL2.text.toString()
            val l2PF = binding.tvPfL2.text.toString()
            val l2Current = binding.tvCurrentL2.text.toString()

            val l3Wattage = binding.tvWattageL3.text.toString()
            val l3PF = binding.tvPfL3.text.toString()
            val l3Current = binding.tvCurrentL3.text.toString()

            val l4Wattage = binding.tvWattageL4.text.toString()
            val l4PF = binding.tvPfL4.text.toString()
            val l4Current = binding.tvCurrentL4.text.toString()
            Log.d(TAG, faultyChannelDetailsJsonObject.toString())

            viewModel.getLoggedInUser().observe(this) { user ->
                if (user != null) {
                    viewModel.updateWiringDeviceFormData(
                        mTicketId,
                        user.UserId ?: 0,
                        0,
                        phaseIds.filter { !it.isWhitespace() },
                        supplyIds.filter { !it.isWhitespace() },
                        voltageJsonArray.toString(),
                        channelJsonArray.toString(),
                        loadDescriptionJsonArray.toString(),
                        typeLedJsonArray.toString(),
                        powerFactor,
                        brandName,
                        shortRemark,
                        l1Wattage,
                        l1PF,
                        l1Current,
                        l2Wattage,
                        l2PF,
                        l2Current,
                        l3Wattage,
                        l3PF,
                        l3Current,
                        l4Wattage,
                        l4PF,
                        l4Current
                    )
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getJsonArrayForVoltage(): JSONArray {
        val postList = ArrayList<Voltage>()
        val jsonArray = JSONArray()
        binding.llVoltage.children.forEach {
            val jsonObject = JSONObject()
            val item = it.spinner.selectedItem as Voltage
            jsonObject.put("VoltageID", item.voltageID)
            jsonObject.put("VoltageName", item.voltageName)

            val itemRemarks = it.etRemarks.text.toString()
            item.voltageRemark = itemRemarks
            jsonObject.put("VoltageRemark", item.voltageRemark)
            postList.add(item)
            jsonArray.put(jsonObject)

        }
        Log.d(TAG, jsonArray.toString())
        return jsonArray
    }

    private fun getJsonArrayFaultyChannel(): JSONArray {
        val postList = ArrayList<FaultyChannel>()
        val jsonArray = JSONArray()
        binding.llFaultyChannel.children.forEach {
            val jsonObject = JSONObject()
            val item = it.spinner.selectedItem as FaultyChannel
            jsonObject.put("FaultyChannelID", item.faultyChannelID)
            jsonObject.put("FaultyChannelName", item.faultyChannelName)

            val itemRemarks = it.etRemarks.text.toString()
            item.faultyChannelRemark = itemRemarks

            jsonObject.put("FaultyChannelRemark", item.faultyChannelRemark)
            postList.add(item)
            jsonArray.put(jsonObject)
        }
        Log.d(TAG, jsonArray.toString())
        return jsonArray
    }

    private fun getJsonArrayLoadDescription(): JSONArray {
        val postList = ArrayList<LoadDescription>()
        val jsonArray = JSONArray()
        binding.llLoadDescription.children.forEach {
            val jsonObject = JSONObject()
            val item = it.spinner.selectedItem as LoadDescription
            jsonObject.put("LoadDescriptionID", item.loadDescriptionID)
            jsonObject.put("LoadDescriptionName", item.loadDescriptionName)

            val itemRemarks = it.etRemarks.text.toString()
            item.loadDescriptionRemark = itemRemarks

            jsonObject.put("LoadDescriptionRemark", item.loadDescriptionRemark)
            postList.add(item)
            jsonArray.put(jsonObject)
        }
        Log.d(TAG, jsonArray.toString())
        return jsonArray
    }

    private fun getJsonArrayTypeLed(): JSONArray {
        val postList = ArrayList<TypeLED>()
        val jsonArray = JSONArray()
        binding.llTypeLed.children.forEach {
            val jsonObject = JSONObject()
            val item = it.spinner.selectedItem as TypeLED
            jsonObject.put("TypeofLEDID", item.typeofLEDID)
            jsonObject.put("TypeofLEDName", item.typeofLEDName)

            val itemRemarks = it.etRemarks.text.toString()
            item.typeofLEDRemark = itemRemarks

            jsonObject.put("TypeofLEDRemark", item.typeofLEDRemark)
            postList.add(item)
            jsonArray.put(jsonObject)
        }
        Log.d(TAG, jsonArray.toString())
        return jsonArray
    }

    private fun getFaultyChannelDetailsJsonObject(): JSONObject {
        val l1Wattage = binding.tvWattageL1.text.toString()
        val l1PF = binding.tvPfL1.text.toString()
        val l1Current = binding.tvCurrentL1.text.toString()

        val l2Wattage = binding.tvWattageL2.text.toString()
        val l2PF = binding.tvPfL2.text.toString()
        val l2Current = binding.tvCurrentL2.text.toString()

        val l3Wattage = binding.tvWattageL3.text.toString()
        val l3PF = binding.tvPfL3.text.toString()
        val l3Current = binding.tvCurrentL3.text.toString()

        val l4Wattage = binding.tvWattageL4.text.toString()
        val l4PF = binding.tvPfL4.text.toString()
        val l4Current = binding.tvCurrentL4.text.toString()

        val faultyChannelDetails = FaultyChannelDetails(
            l1Current,
            l1PF,
            l1Wattage,
            l2Current,
            l2PF,
            l2Wattage,
            l3Current,
            l3PF,
            l3Wattage,
            l4Current,
            l4PF,
            l4Wattage
        )
        val jsonObject = Gson().toJson(faultyChannelDetails)
        return JSONObject(jsonObject)
    }

    private fun showPointsInputDialog(pointType: PointTableType) {
        val dialogAmountCustomBinding: WiringDevicePointDialogBinding =
            WiringDevicePointDialogBinding.inflate(LayoutInflater.from(this))
        val dialog: AlertDialog = MaterialAlertDialogBuilder(
            this,
            R.style.MyRounded_MaterialComponents_MaterialAlertDialog
        )
            .setView(dialogAmountCustomBinding.root).show()
        dialog.setCancelable(false)

        val defaultValue = getTableValue(pointType)
        dialogAmountCustomBinding.etAmount.setText(defaultValue)

        dialogAmountCustomBinding.ivClose.setOnClickListener { dialog.dismiss() }
        dialogAmountCustomBinding.btnOk.setOnClickListener {
            val pointValue = dialogAmountCustomBinding.etAmount.text.toString()

            when (pointType) {
                PointTableType.WATTAGE_L1 -> {
                    binding.tvWattageL1.text = pointValue
                    binding.tlPoints.requestFocus()
                }
                PointTableType.PF_L1 -> {
                    binding.tvPfL1.text = pointValue
                    binding.tlPoints.requestFocus()
                }
                PointTableType.CURRENT_L1 -> {
                    binding.tvCurrentL1.text = pointValue
                    binding.tlPoints.requestFocus()
                }

                PointTableType.WATTAGE_L2 -> {
                    binding.tvWattageL2.text = pointValue
                    binding.tlPoints.requestFocus()
                }
                PointTableType.PF_L2 -> {
                    binding.tvPfL2.text = pointValue
                    binding.tlPoints.requestFocus()
                }
                PointTableType.CURRENT_L2 -> {
                    binding.tvCurrentL2.text = pointValue
                    binding.tlPoints.requestFocus()
                }

                PointTableType.WATTAGE_L3 -> {
                    binding.tvWattageL3.text = pointValue
                    binding.tlPoints.requestFocus()
                }
                PointTableType.PF_L3 -> {
                    binding.tvPfL3.text = pointValue
                    binding.tlPoints.requestFocus()
                }
                PointTableType.CURRENT_L3 -> {
                    binding.tvCurrentL3.text = pointValue
                    binding.tlPoints.requestFocus()
                }

                PointTableType.WATTAGE_L4 -> {
                    binding.tvWattageL4.text = pointValue
                    binding.tlPoints.requestFocus()
                }
                PointTableType.PF_L4 -> {
                    binding.tvPfL4.text = pointValue
                    binding.tlPoints.requestFocus()
                }
                PointTableType.CURRENT_L4 -> {
                    binding.tvCurrentL4.text = pointValue
                    binding.tlPoints.requestFocus()
                }
            }

            dialog.dismiss()
        }
    }

    private fun getTableValue(pointType: PointTableType): String {
        when (pointType) {
            PointTableType.WATTAGE_L1 -> {
                return binding.tvWattageL1.text.toString()
            }
            PointTableType.PF_L1 -> {
                return binding.tvPfL1.text.toString()
            }
            PointTableType.CURRENT_L1 -> {
                return binding.tvCurrentL1.text.toString()
            }

            PointTableType.WATTAGE_L2 -> {
                return binding.tvWattageL2.text.toString()
            }
            PointTableType.PF_L2 -> {
                return binding.tvPfL2.text.toString()
            }
            PointTableType.CURRENT_L2 -> {
                return binding.tvCurrentL2.text.toString()
            }

            PointTableType.WATTAGE_L3 -> {
                return binding.tvWattageL3.text.toString()
            }
            PointTableType.PF_L3 -> {
                return binding.tvPfL3.text.toString()
            }
            PointTableType.CURRENT_L3 -> {
                return binding.tvCurrentL3.text.toString()
            }

            PointTableType.WATTAGE_L4 -> {
                return binding.tvWattageL4.text.toString()
            }
            PointTableType.PF_L4 -> {
                return binding.tvPfL4.text.toString()
            }
            PointTableType.CURRENT_L4 -> {
                return binding.tvCurrentL4.text.toString()
            }
        }
    }

    private fun addVoltageView(list: ArrayList<Voltage>, newView: Boolean) {
        if (list.isNotEmpty()) {
            // if form already submitted before
            if (newView) {
                // if child count in voltage linear layout is less than list size than only add new view
                if (binding.llVoltage.childCount < list.size) {
                    val newVoltageView = MultiSelectionItemLayout(this, null)
                    voltageViewCount++
                    binding.llVoltage.addView(newVoltageView)
                    newVoltageView.binding.spinner.attachDataSource(list)
                    newVoltageView.ivCancel.setOnClickListener {
                        if (voltageViewCount > 1) {
                            binding.llVoltage.removeView(newVoltageView)
                            voltageViewCount--
                        }
                    }
                }
            } else {
                list.forEachIndexed { index, voltage ->
                    if (voltage.voltageRemark.isNotEmpty()) {
                        val newVoltageView = MultiSelectionItemLayout(this, null)
                        newVoltageView.binding.spinner.attachDataSource(list)
                        newVoltageView.binding.spinner.selectedIndex = index
                        newVoltageView.binding.etRemarks.setText(voltage.voltageRemark)
                        voltageViewCount++
                        binding.llVoltage.addView(newVoltageView)
                        newVoltageView.ivCancel.setOnClickListener {
                            if (voltageViewCount > 1) {
                                binding.llVoltage.removeView(newVoltageView)
                                voltageViewCount--
                            }
                        }
                    }
                }
                if (binding.llVoltage.childCount == 0) {
                    addVoltageView(list, true)
                }
            }
        }

    }

    private fun addTypeLedView(list: ArrayList<TypeLED>, newView: Boolean) {
        if (list.isNotEmpty()) {
            if (newView) {
                if (binding.llTypeLed.childCount < list.size) {
                    val newTypeLedView = MultiSelectionItemLayout(this, null)
                    typeLedViewCount++
                    binding.llTypeLed.addView(newTypeLedView)
                    newTypeLedView.binding.spinner.attachDataSource(list)
                    newTypeLedView.ivCancel.setOnClickListener {
                        if (typeLedViewCount > 1) {
                            binding.llTypeLed.removeView(newTypeLedView)
                            typeLedViewCount--
                        }
                    }

                }
            } else {
                list.forEachIndexed { index, typeLED ->
                    if (typeLED.typeofLEDRemark.isNotEmpty()) {
                        val newTypeLedView = MultiSelectionItemLayout(this, null)
                        newTypeLedView.binding.spinner.attachDataSource(list)
                        newTypeLedView.binding.spinner.selectedIndex = index
                        newTypeLedView.binding.etRemarks.setText(typeLED.typeofLEDRemark)
                        typeLedViewCount++
                        binding.llTypeLed.addView(newTypeLedView)
                        newTypeLedView.ivCancel.setOnClickListener {
                            if (typeLedViewCount > 1) {
                                binding.llTypeLed.removeView(newTypeLedView)
                                typeLedViewCount--
                            }
                        }
                    }
                }
                if (binding.llTypeLed.childCount == 0) {
                    addTypeLedView(list, true)
                }
            }
        }
    }

    private fun addLoadDescriptionView(list: ArrayList<LoadDescription>, newView: Boolean) {
        if (list.isNotEmpty()) {
            if (newView) {
                if (binding.llLoadDescription.childCount < list.size) {
                    val newLoadDescriptionView = MultiSelectionItemLayout(this, null)
                    loadDescriptionViewCount++
                    binding.llLoadDescription.addView(newLoadDescriptionView)
                    newLoadDescriptionView.binding.spinner.attachDataSource(list)
                    newLoadDescriptionView.ivCancel.setOnClickListener {
                        if (loadDescriptionViewCount > 1) {
                            binding.llLoadDescription.removeView(newLoadDescriptionView)
                            loadDescriptionViewCount--
                        }
                    }
                }
            } else {
                list.forEachIndexed { index, loadDescription ->
                    if (loadDescription.loadDescriptionRemark.isNotEmpty()) {
                        val newLoadDescriptionView = MultiSelectionItemLayout(this, null)
                        newLoadDescriptionView.binding.spinner.attachDataSource(list)
                        newLoadDescriptionView.binding.spinner.selectedIndex = index
                        newLoadDescriptionView.binding.etRemarks.setText(loadDescription.loadDescriptionRemark)
                        loadDescriptionViewCount++
                        binding.llLoadDescription.addView(newLoadDescriptionView)
                        newLoadDescriptionView.ivCancel.setOnClickListener {
                            if (loadDescriptionViewCount > 1) {
                                binding.llLoadDescription.removeView(newLoadDescriptionView)
                                loadDescriptionViewCount--
                            }
                        }
                    }
                }
                if (binding.llLoadDescription.childCount == 0) {
                    addLoadDescriptionView(list, true)
                }
            }
        }
    }

    private fun addFaultyChannelView(list: ArrayList<FaultyChannel>, newView: Boolean) {
        if (list.isNotEmpty()) {
            if (newView) {
                if (binding.llFaultyChannel.childCount < list.size) {
                    val newFaultyChannelView = MultiSelectionItemLayout(this, null)
                    channelViewCount++
                    binding.llFaultyChannel.addView(newFaultyChannelView)
                    newFaultyChannelView.binding.spinner.attachDataSource(list)
                    newFaultyChannelView.ivCancel.setOnClickListener {
                        if (channelViewCount > 1) {
                            binding.llFaultyChannel.removeView(newFaultyChannelView)
                            channelViewCount--
                        }
                    }
                }
            } else {
                list.forEachIndexed { index, faultyChannel ->
                    if (faultyChannel.faultyChannelRemark.isNotEmpty()) {
                        val newFaultyChannelView = MultiSelectionItemLayout(this, null)
                        newFaultyChannelView.binding.spinner.attachDataSource(list)
                        newFaultyChannelView.binding.spinner.selectedIndex = index
                        newFaultyChannelView.binding.etRemarks.setText(faultyChannel.faultyChannelRemark)
                        channelViewCount++
                        binding.llFaultyChannel.addView(newFaultyChannelView)
                        newFaultyChannelView.ivCancel.setOnClickListener {
                            if (channelViewCount > 1) {
                                binding.llFaultyChannel.removeView(newFaultyChannelView)
                                channelViewCount--
                            }
                        }
                    }
                }
                //If no data from api, add new view
                if (binding.llFaultyChannel.childCount == 0) {
                    addFaultyChannelView(list, true)
                }
            }
        }
    }

    override fun onStarted(callFrom: String) {
        binding.progressBar.start()
    }

    override fun onSuccess(_object: List<Any?>, callFrom: String) {
        binding.progressBar.stop()

        if (callFrom == GlobalConstant.GET_WIRING_DEVICES_FORM_API) {
            val wiringDeviceData = _object as List<WiringDeviceData>
            setFormDataFromApi(wiringDeviceData)
        } else if (callFrom == GlobalConstant.UPDATE_WIRING_DEVICES_FORM_API) {
            val wiringDeviceResponse = _object as List<WiringDeviceUpdateDataItem>
            val message = wiringDeviceResponse[0].statusMessage
            toast(message)
            finish()
        }
    }

    override fun onError(message: String, callFrom: String, isNetworkError: Boolean) {
        binding.progressBar.stop()
        binding.rlWiringDeviceForm.snackbar(message)
    }

    override fun onValidationError(message: String, callFrom: String) {
        binding.rlWiringDeviceForm.snackbar(message)
    }

    private fun setFormDataFromApi(wiringDeviceData: List<WiringDeviceData>) {
        if (wiringDeviceData.isNotEmpty()) {
            phaseList = wiringDeviceData[0].phaseList as ArrayList<Phase>
            setPhaseData()

            supplyList = wiringDeviceData[0].supplyList as ArrayList<Supply>
            setSupplyData()

            voltageList = wiringDeviceData[0].voltageList as ArrayList<Voltage>
            addVoltageView(voltageList, false)

            faultyChannelList = wiringDeviceData[0].faultyChannelList as ArrayList<FaultyChannel>
            addFaultyChannelView(faultyChannelList, false)

            loadDescriptionList = wiringDeviceData[0].loadDescriptionList as ArrayList<LoadDescription>
            addLoadDescriptionView(loadDescriptionList, false)

            typeLedList = wiringDeviceData[0].typeofLEDList as ArrayList<TypeLED>
            addTypeLedView(typeLedList, false)

            binding.tietPowerFactor.setText(wiringDeviceData[0].powerFactor)
            binding.tietDetails.setText(wiringDeviceData[0].shortRemark)
            binding.tietMake.setText(wiringDeviceData[0].brandName)

            binding.tvWattageL1.text = wiringDeviceData[0].faultyChannelDetails.l1Wattage
            binding.tvWattageL2.text = wiringDeviceData[0].faultyChannelDetails.l2Wattage
            binding.tvWattageL3.text = wiringDeviceData[0].faultyChannelDetails.l3Wattage
            binding.tvWattageL4.text = wiringDeviceData[0].faultyChannelDetails.l4Wattage

            binding.tvPfL1.text = wiringDeviceData[0].faultyChannelDetails.l1PF
            binding.tvPfL2.text = wiringDeviceData[0].faultyChannelDetails.l2PF
            binding.tvPfL3.text = wiringDeviceData[0].faultyChannelDetails.l3PF
            binding.tvPfL4.text = wiringDeviceData[0].faultyChannelDetails.l4PF

            binding.tvCurrentL1.text = wiringDeviceData[0].faultyChannelDetails.l1Current
            binding.tvCurrentL2.text = wiringDeviceData[0].faultyChannelDetails.l2Current
            binding.tvCurrentL3.text = wiringDeviceData[0].faultyChannelDetails.l3Current
            binding.tvCurrentL4.text = wiringDeviceData[0].faultyChannelDetails.l4Current
        }
    }

    private fun setPhaseData() {
        for (phase in phaseList) {
            if (phase.phaseID == 1) {
                binding.cbPhaseSingle.isChecked = true
            } else if (phase.phaseID == 2) {
                binding.cbPhaseThree.isChecked = true
            }
        }
    }

    private fun setSupplyData() {
        for (supply in supplyList) {
            when (supply.supplyID) {
                3 -> {
                    binding.cbSupplyNormal.isChecked = true
                }
                4 -> {
                    binding.cbSupplyInverter.isChecked = true
                }
                5 -> {
                    binding.cbSupplyGenerator.isChecked = true
                }
            }
        }
    }

    companion object {
        fun start(context: Context, ticketId: Int) {
            val intent = Intent(context, WiringDeviceFormActivity::class.java)
            intent.putExtra(TICKET_ID, ticketId)
            context.startActivity(intent)
        }
    }

}