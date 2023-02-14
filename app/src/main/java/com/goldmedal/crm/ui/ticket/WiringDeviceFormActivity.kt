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
    private var _binding: ActivityWiringDeviceFormBinding? = null
    private val binding get() = _binding!!
    private val factory: TicketViewModelFactory by instance()
    private lateinit var viewModel: TicketViewModel
    private var phaseList = mutableListOf<String>()
    private var supplyList = mutableListOf<String>()
    private var voltageViewCount = 1
    private var channelViewCount = 1
    private var loadDescriptionViewCount = 1
    private var typeLedViewCount = 1
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
                voltageViewCount++
                addVoltageView(voltageList)
            }
        }
        binding.viewFaultyChannelAdd.setOnClickListener {
            if (faultyChannelList.isNotEmpty() && channelViewCount < faultyChannelList.size) {
                addFaultyChannelView(faultyChannelList)
                channelViewCount++
            }
        }
        binding.viewLoadDescriptionAdd.setOnClickListener {
            if (loadDescriptionList.isNotEmpty() && loadDescriptionViewCount < loadDescriptionList.size) {
                addLoadDescriptionView(loadDescriptionList)
                loadDescriptionViewCount++
            }
        }
        binding.viewTypeLedAdd.setOnClickListener {
            if (typeLedList.isNotEmpty() && typeLedViewCount < typeLedList.size) {
                addTypeLedView(typeLedList)
                typeLedViewCount++
            }
        }

        binding.tvVoltageClearAll.setOnClickListener {
            voltageViewCount = 1
            binding.llVoltage.removeAllViews()
            isClearAll = true
            addVoltageView(voltageList)
        }
        binding.tvFaultyChannelClearAll.setOnClickListener {
            channelViewCount = 1
            binding.llFaultyChannel.removeAllViews()
            isClearAll = true
            addFaultyChannelView(faultyChannelList)
        }
        binding.tvLoadDescriptionClearAll.setOnClickListener {
            loadDescriptionViewCount = 1
            binding.llLoadDescription.removeAllViews()
            isClearAll = true
            addLoadDescriptionView(loadDescriptionList)
        }
        binding.tvTypeLedClearAll.setOnClickListener {
            typeLedViewCount = 1
            binding.llTypeLed.removeAllViews()
            isClearAll = true
            addTypeLedView(typeLedList)
        }
        binding.btnSubmit.setOnClickListener {
            prepareDataAndCallApi()
        }
    }

    private fun prepareDataAndCallApi() {
        val phaseList = mutableListOf<String>()
        if (binding.cbPhaseSingle.isChecked) {
            phaseList.add("Single")
        }
        if (binding.cbPhaseThree.isChecked) {
            phaseList.add("Three Phase")
        }
        Log.d("Arun", phaseList.joinToString { it })
        if (phaseList.isEmpty()) {
            Toast.makeText(this, "Select Phase Item", Toast.LENGTH_SHORT).show()
            return
        }

        val supplyList = mutableListOf<String>()
        if (binding.cbSupplyNormal.isChecked) {
            supplyList.add("Normal")
        }
        if (binding.cbSupplyInverter.isChecked) {
            supplyList.add("Inverter")
        }
        if (binding.cbSupplyGenerator.isChecked) {
            supplyList.add("Generator")
        }
        Log.d("Arun", supplyList.joinToString { it })
        if (supplyList.isEmpty()) {
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
        Log.d("Arun", faultyChannelDetailsJsonObject.toString())

        viewModel.getLoggedInUser().observe(this) { user ->
            if (user != null) {
                viewModel.updateWiringDeviceFormData(
                    mTicketId,
                    user.UserId ?: 0,
                    0,
                    phaseList.joinToString { it },
                    supplyList.joinToString { it },
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
    }

    private fun getJsonArrayForVoltage(): JSONArray {
        val postList = ArrayList<Voltage>()
        val jsonArray = JSONArray()
        binding.llVoltage.children.forEach {
            val jsonObject = JSONObject()
            val item = it.spinner.selectedItem as Voltage
            jsonObject.put("VoltageID", item.voltageID)
            jsonObject.put("VoltageName", item.voltageName)
            jsonObject.put("VoltageRemark", item.voltageRemark)
            val itemRemarks = it.etRemarks.text.toString()
            if (itemRemarks.isNotEmpty()) {
                item.voltageRemark = itemRemarks
            }
            postList.add(item)
            jsonArray.put(jsonObject.toString())

        }
        val jsArray = Gson().toJson(postList)
        Log.d("Arun", jsonArray.toString())
        //return JSONArray(jsArray)
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
            jsonObject.put("FaultyChannelRemark", item.faultyChannelRemark)
            val itemRemarks = it.etRemarks.text.toString()
            if (itemRemarks.isNotEmpty()) {
                item.faultyChannelRemark = itemRemarks
            }
            postList.add(item)
            jsonArray.put(jsonObject.toString())
        }
        val jsArray = Gson().toJson(postList)
        Log.d("Arun", jsonArray.toString())
        //return JSONArray(jsArray)
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
            jsonObject.put("LoadDescriptionRemark", item.loadDescriptionRemark)
            val itemRemarks = it.etRemarks.text.toString()
            if (itemRemarks.isNotEmpty()) {
                item.loadDescriptionRemark = itemRemarks
            }
            postList.add(item)
            jsonArray.put(jsonObject.toString())
        }
        val jsArray = Gson().toJson(postList)
        Log.d("Arun", jsonArray.toString())
        //return JSONArray(jsArray)
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
            jsonObject.put("TypeofLEDRemark", item.typeofLEDRemark)
            val itemRemarks = it.etRemarks.text.toString()
            if (itemRemarks.isNotEmpty()) {
                item.typeofLEDRemark = itemRemarks
            }
            postList.add(item)
            jsonArray.put(jsonObject.toString())
        }
        val jsArray = Gson().toJson(postList)
        Log.d("Arun", jsonArray.toString())
        //return JSONArray(jsArray)
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

        dialogAmountCustomBinding.ivClose.setOnClickListener { dialog.dismiss() }
        dialogAmountCustomBinding.btnOk.setOnClickListener {
            val pointValue = dialogAmountCustomBinding.etAmount.text.toString()
            if (pointValue.isNotEmpty()) {
                when (pointType) {
                    PointTableType.WATTAGE_L1 -> {
                        binding.tvWattageL1.text = pointValue
                        binding.tvPfL1.requestFocus()
                    }
                    PointTableType.PF_L1 -> {
                        binding.tvPfL1.text = pointValue
                    }
                    PointTableType.CURRENT_L1 -> {
                        binding.tvCurrentL1.text = pointValue
                    }

                    PointTableType.WATTAGE_L2 -> {
                        binding.tvWattageL2.text = pointValue
                    }
                    PointTableType.PF_L2 -> {
                        binding.tvPfL2.text = pointValue
                    }
                    PointTableType.CURRENT_L2 -> {
                        binding.tvCurrentL2.text = pointValue
                    }

                    PointTableType.WATTAGE_L3 -> {
                        binding.tvWattageL3.text = pointValue
                    }
                    PointTableType.PF_L3 -> {
                        binding.tvPfL3.text = pointValue
                    }
                    PointTableType.CURRENT_L3 -> {
                        binding.tvCurrentL3.text = pointValue
                    }

                    PointTableType.WATTAGE_L4 -> {
                        binding.tvWattageL4.text = pointValue
                    }
                    PointTableType.PF_L4 -> {
                        binding.tvPfL4.text = pointValue
                    }
                    PointTableType.CURRENT_L4 -> {
                        binding.tvCurrentL4.text = pointValue
                    }
                }
            }
            dialog.dismiss()
        }
    }

    private fun addVoltageView(list: ArrayList<Voltage>) {
        // todo - need to test from api data
        if (list.isNotEmpty()) {
            // if form already submitted before
            if (!isClearAll) {
                list.forEachIndexed { index, voltage ->
                    if (voltage.voltageRemark.isNotEmpty()) {
                        val newVoltageView = MultiSelectionItemLayout(this, null)
                        newVoltageView.binding.spinner.attachDataSource(list)
                        newVoltageView.binding.spinner.selectedIndex = index
                        newVoltageView.binding.etRemarks.setText(voltage.voltageRemark)
                        voltageViewCount++
                        binding.llVoltage.addView(newVoltageView)
                        newVoltageView.ivCancel.setOnClickListener {
                            binding.llVoltage.removeView(newVoltageView)
                            voltageViewCount--
                        }
                    }
                }
            }

            isClearAll = false

            // if child count in voltage linear layout is less than list size than only add new view
            if (binding.llVoltage.childCount < list.size) {
                val newVoltageView = MultiSelectionItemLayout(this, null)
                binding.llVoltage.addView(newVoltageView)
                newVoltageView.ivCancel.setOnClickListener {
                    binding.llVoltage.removeView(newVoltageView)
                    voltageViewCount--
                }
                newVoltageView.binding.spinner.attachDataSource(list)
            }
        }

    }

    private fun addTypeLedView(list: ArrayList<TypeLED>) {
        if (list.isNotEmpty()) {
            if (!isClearAll) {
                list.forEachIndexed { index, typeLED ->
                    if (typeLED.typeofLEDRemark.isNotEmpty()) {
                        val newTypeLedView = MultiSelectionItemLayout(this, null)
                        newTypeLedView.binding.spinner.attachDataSource(list)
                        newTypeLedView.binding.spinner.selectedIndex = index
                        newTypeLedView.binding.etRemarks.setText(typeLED.typeofLEDRemark)
                        typeLedViewCount++
                        binding.llTypeLed.addView(newTypeLedView)
                        newTypeLedView.ivCancel.setOnClickListener {
                            binding.llTypeLed.removeView(newTypeLedView)
                            typeLedViewCount--
                        }
                    }
                }
            }
            isClearAll = false

            if (binding.llTypeLed.childCount < list.size) {
                val newTypeLedView = MultiSelectionItemLayout(this, null)
                binding.llTypeLed.addView(newTypeLedView)
                newTypeLedView.ivCancel.setOnClickListener {
                    binding.llTypeLed.removeView(newTypeLedView)
                    typeLedViewCount--
                }
                newTypeLedView.binding.spinner.attachDataSource(list)
            }
        }
    }

    private fun addLoadDescriptionView(list: ArrayList<LoadDescription>) {
        if (list.isNotEmpty()) {
            if (!isClearAll) {
                list.forEachIndexed { index, loadDescription ->
                    if (loadDescription.loadDescriptionRemark.isNotEmpty()) {
                        val newLoadDescriptionView = MultiSelectionItemLayout(this, null)
                        newLoadDescriptionView.binding.spinner.attachDataSource(list)
                        newLoadDescriptionView.binding.spinner.selectedIndex = index
                        newLoadDescriptionView.binding.etRemarks.setText(loadDescription.loadDescriptionRemark)
                        loadDescriptionViewCount++
                        binding.llLoadDescription.addView(newLoadDescriptionView)
                        newLoadDescriptionView.ivCancel.setOnClickListener {
                            binding.llLoadDescription.removeView(newLoadDescriptionView)
                            loadDescriptionViewCount--
                        }
                    }
                }
            }
            isClearAll = false

            if (binding.llLoadDescription.childCount < list.size) {
                val newLoadDescriptionView = MultiSelectionItemLayout(this, null)
                binding.llLoadDescription.addView(newLoadDescriptionView)
                newLoadDescriptionView.ivCancel.setOnClickListener {
                    binding.llLoadDescription.removeView(newLoadDescriptionView)
                    loadDescriptionViewCount--
                }
                newLoadDescriptionView.binding.spinner.attachDataSource(list)
            }
        }
    }

    private fun addFaultyChannelView(list: ArrayList<FaultyChannel>) {
        if (list.isNotEmpty()) {
            if (!isClearAll) {
                list.forEachIndexed { index, faultyChannel ->
                    if (faultyChannel.faultyChannelRemark.isNotEmpty()) {
                        val newFaultyChannelView = MultiSelectionItemLayout(this, null)
                        newFaultyChannelView.binding.spinner.attachDataSource(list)
                        newFaultyChannelView.binding.spinner.selectedIndex = index
                        newFaultyChannelView.binding.etRemarks.setText(faultyChannel.faultyChannelRemark)
                        channelViewCount++
                        binding.llFaultyChannel.addView(newFaultyChannelView)
                        newFaultyChannelView.ivCancel.setOnClickListener {
                            binding.llFaultyChannel.removeView(newFaultyChannelView)
                            channelViewCount--
                        }
                    }
                }
            }
            isClearAll = false

            if (binding.llFaultyChannel.childCount < list.size) {
                val newFaultyChannelView = MultiSelectionItemLayout(this, null)
                binding.llFaultyChannel.addView(newFaultyChannelView)
                newFaultyChannelView.ivCancel.setOnClickListener {
                    binding.llFaultyChannel.removeView(newFaultyChannelView)
                    channelViewCount--
                }
                newFaultyChannelView.binding.spinner.attachDataSource(list)
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
            voltageList = wiringDeviceData[0].voltageList as ArrayList<Voltage>
            addVoltageView(voltageList)

            faultyChannelList = wiringDeviceData[0].faultyChannelList as ArrayList<FaultyChannel>
            addFaultyChannelView(faultyChannelList)

            loadDescriptionList =
                wiringDeviceData[0].loadDescriptionList as ArrayList<LoadDescription>
            addLoadDescriptionView(loadDescriptionList)

            typeLedList = wiringDeviceData[0].typeofLEDList as ArrayList<TypeLED>
            addTypeLedView(typeLedList)
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