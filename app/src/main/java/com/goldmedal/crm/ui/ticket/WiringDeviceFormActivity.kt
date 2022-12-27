package com.goldmedal.crm.ui.ticket

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.goldmedal.crm.R
import com.goldmedal.crm.databinding.ActivityWiringDeviceFormBinding
import com.goldmedal.crm.databinding.WiringDevicePointDialogBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein

enum class PointTableType {
    WATTAGE_L1,
    WATTAGE_L2,
    WATTAGE_L3,
    PF_L1,
    PF_L2,
    PF_L3,
    CURRENT_L1,
    CURRENT_L2,
    CURRENT_L3
}

class WiringDeviceFormActivity : AppCompatActivity(), KodeinAware {
    override val kodein by kodein()
    private var _binding: ActivityWiringDeviceFormBinding? = null
    private val binding get() = _binding!!
    private var voltageViewCount = 1
    private var channelViewCount = 1
    private var loadDescriptionViewCount = 1
    private var typeLedViewCount = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityWiringDeviceFormBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
        setClickListeners()
    }

    private fun initViews() {
        addVoltageView()
        addFaultyChannelView()
        addLoadDescriptionView()
        addTypeLedView()
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
        binding.tvPfL1.setOnClickListener { showPointsInputDialog(PointTableType.PF_L1) }
        binding.tvPfL2.setOnClickListener { showPointsInputDialog(PointTableType.PF_L2) }
        binding.tvPfL3.setOnClickListener { showPointsInputDialog(PointTableType.PF_L3) }
        binding.tvCurrentL1.setOnClickListener { showPointsInputDialog(PointTableType.CURRENT_L1) }
        binding.tvCurrentL2.setOnClickListener { showPointsInputDialog(PointTableType.CURRENT_L2) }
        binding.tvCurrentL3.setOnClickListener { showPointsInputDialog(PointTableType.CURRENT_L3) }

        binding.viewVoltageAdd.setOnClickListener { addVoltageView() }
        binding.viewFaultyChannelAdd.setOnClickListener { addFaultyChannelView() }
        binding.viewLoadDescriptionAdd.setOnClickListener { addLoadDescriptionView() }
        binding.viewTypeLedAdd.setOnClickListener { addTypeLedView() }

        binding.tvVoltageClearAll.setOnClickListener {
            binding.llVoltage.removeAllViews()
            addVoltageView()
        }
        binding.tvFaultyChannelClearAll.setOnClickListener {
            binding.llFaultyChannel.removeAllViews()
            addFaultyChannelView()
        }
        binding.tvLoadDescriptionClearAll.setOnClickListener {
            binding.llLoadDescription.removeAllViews()
            addLoadDescriptionView()
        }
        binding.tvTypeLedClearAll.setOnClickListener {
            binding.llTypeLed.removeAllViews()
            addTypeLedView()
        }
    }

    private fun showPointsInputDialog(pointType: PointTableType) {
        val dialogAmountCustomBinding: WiringDevicePointDialogBinding = WiringDevicePointDialogBinding.inflate(LayoutInflater.from(this))
        val dialog: AlertDialog = MaterialAlertDialogBuilder(this, R.style.MyRounded_MaterialComponents_MaterialAlertDialog)
            .setView(dialogAmountCustomBinding.root).show()
        dialog.setCancelable(true)
    }

    private fun addVoltageView() {
        val newVoltageView = MultiSelectionItemLayout(this, null)
        binding.llVoltage.addView(newVoltageView)

        val voltageArray: MutableList<String> = ArrayList()
        voltageArray.add("P-P")
        voltageArray.add("P-N")
        voltageArray.add("P-G")
        voltageArray.add("N-G")
        newVoltageView.binding.spinner.attachDataSource(voltageArray)
    }

    private fun addTypeLedView() {
        val newTypeLedView = MultiSelectionItemLayout(this, null)
        binding.llTypeLed.addView(newTypeLedView)

        val typeLedArray: MutableList<String> = ArrayList()
        typeLedArray.add("Down Light")
        typeLedArray.add("LED Strip")
        typeLedArray.add("COB")
        newTypeLedView.binding.spinner.attachDataSource(typeLedArray)
    }

    private fun addLoadDescriptionView() {
        val newLoadDescriptionView = MultiSelectionItemLayout(this, null)
        binding.llLoadDescription.addView(newLoadDescriptionView)

        val loadDescriptionArray: MutableList<String> = ArrayList()
        loadDescriptionArray.add("LED")
        loadDescriptionArray.add("CFL")
        loadDescriptionArray.add("Halogen")
        loadDescriptionArray.add("Tungsten")
        loadDescriptionArray.add("TV")
        newLoadDescriptionView.binding.spinner.attachDataSource(loadDescriptionArray)
    }

    private fun addFaultyChannelView() {
        val newFaultyChannelView = MultiSelectionItemLayout(this, null)
        binding.llFaultyChannel.addView(newFaultyChannelView)

        val faultyChannelArray: MutableList<String> = ArrayList()
        faultyChannelArray.add("L1")
        faultyChannelArray.add("L2")
        faultyChannelArray.add("L3")
        faultyChannelArray.add("L4")
        newFaultyChannelView.binding.spinner.attachDataSource(faultyChannelArray)
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, WiringDeviceFormActivity::class.java))
        }
    }
}