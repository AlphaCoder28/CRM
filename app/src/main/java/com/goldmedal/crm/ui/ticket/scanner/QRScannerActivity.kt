package com.goldmedal.crm.ui.ticket.scanner

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.goldmedal.crm.databinding.ActivityQrscannerBinding
import com.goldmedal.crm.util.BARCODE_RESULT_KEY
import com.goldmedal.crm.util.toast
import io.github.g00fy2.quickie.QRResult
import io.github.g00fy2.quickie.ScanQRCode

class QRScannerActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityQrscannerBinding
    private val scanQrCodeLauncher = registerForActivityResult(ScanQRCode()) { result ->
        // handle QRResult
        val returnIntent = Intent()
        when (result) {
            is QRResult.QRSuccess -> {
                val content = result.content.rawValue
                returnIntent.putExtra(BARCODE_RESULT_KEY, content)
                setResult(RESULT_OK, returnIntent)
            }
            QRResult.QRUserCanceled -> {
                toast("User canceled")
                setResult(Activity.RESULT_CANCELED, returnIntent)
            }
            QRResult.QRMissingPermission -> {
                toast("Missing permission")
                setResult(Activity.RESULT_CANCELED, returnIntent)
            }
            is QRResult.QRError -> {
                toast("${result.exception.javaClass.simpleName}: ${result.exception.localizedMessage}")
                setResult(Activity.RESULT_CANCELED, returnIntent)
            }
        }
        finish()
    }

    private var requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            scan()
        } else {
            toast("Permission Denied")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityQrscannerBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        checkCameraPermission()
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        } else {
            scan()
        }
    }

    private fun scan() {
        scanQrCodeLauncher.launch(null)
    }
}