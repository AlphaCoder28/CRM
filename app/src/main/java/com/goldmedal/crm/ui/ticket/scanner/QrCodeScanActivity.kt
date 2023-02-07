package com.goldmedal.crm.ui.ticket.scanner

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.*
import com.goldmedal.crm.R
import com.goldmedal.crm.ui.ticket.ComplainTabFragment
import com.goldmedal.crm.util.alertDialog
import com.google.zxing.BarcodeFormat
import com.google.zxing.client.result.ParsedResultType
import com.google.zxing.client.result.ResultParser
import com.google.zxing.client.result.URIParsedResult


class QrCodeScanActivity : AppCompatActivity() {

    private lateinit var codeScanner: CodeScanner
    private var callFrom: String = ""
//    private var cameraProvider: ProcessCameraProvider? = null
//    private var cameraSelector: CameraSelector? = null
//    private var lensFacing = CameraSelector.LENS_FACING_BACK
//    private var previewUseCase: Preview? = null
//    private var analysisUseCase: ImageAnalysis? = null

//    private val screenAspectRatio: Int
//        get() {
//            // Get screen metrics used to setup camera for full screen resolution
//            val metrics = DisplayMetrics().also { previewView?.display?.getRealMetrics(it) }
//            return aspectRatio(metrics.widthPixels , metrics.heightPixels)
//        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_code_scan)
        setupCamera()

        intent.let {
            callFrom = intent.getStringExtra(ARG_CALL_FROM).toString()
        }
    }

    private fun setupCamera() {

        val scannerView = findViewById<CodeScannerView>(R.id.scanner_view)

        codeScanner = CodeScanner(this, scannerView)

        // Parameters (default values)
        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = listOf(BarcodeFormat.QR_CODE)//CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
        codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        codeScanner.isFlashEnabled = false // Whether to enable flash or not

        // Callbacks
        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {

                if(callFrom.equals("scan")){
                    val parsedResult = ResultParser.parseResult(it)
                    when (parsedResult.type) {
                        ParsedResultType.URI -> {
                            val parsedUri = parsedResult as URIParsedResult
                            Log.d(TAG, "uri: "+ parsedResult)

                            val key =
                                Uri.parse(parsedUri.uri).getQueryParameter("key") ?: Uri.parse(
                                    parsedUri.uri
                                ).getQueryParameter("k")
                            val qrCode =
                                Uri.parse(parsedUri.uri).getQueryParameter("qrcode") ?: Uri.parse(
                                    parsedUri.uri
                                ).getQueryParameter("q")
                            val master =
                                Uri.parse(parsedUri.uri).getBooleanQueryParameter("master", false)

                            val returnIntent = Intent()
                            returnIntent.putExtra("key", key)
                            returnIntent.putExtra("qr_code", qrCode)
                            returnIntent.putExtra("master", master)
                            returnIntent.putExtra("callFrom", callFrom)
                            setResult(Activity.RESULT_OK, returnIntent)
                            finish()


                        }


                        else ->  {
                            alertDialog("Invalid Qr Code")
                            finish()
                        }
                    }

                    Toast.makeText(this, "Scan result: ${it.text}", Toast.LENGTH_LONG).show()
                    Log.d(TAG, "text: "+ parsedResult.type)
                }


                if(callFrom.equals("replacement")){
                    val parsedResult = ResultParser.parseResult(it)
                    when (parsedResult.type) {
                        ParsedResultType.TEXT -> {
                       //     val  parsedUri = parsedResult as URIParsedResult

//                            val key =  Uri.parse(parsedUri.uri).getQueryParameter("key")
//                            val qrCode =  Uri.parse(parsedUri.uri).getQueryParameter("qrcode")
//                            val master =  Uri.parse(parsedUri.uri).getBooleanQueryParameter("master",false)

                            val returnIntent = Intent()
                      //      returnIntent.putExtra("key", key)
                            returnIntent.putExtra("qr_code", parsedResult.toString())
                     //       returnIntent.putExtra("master",master)
                            returnIntent.putExtra("callFrom",callFrom)
                            setResult(Activity.RESULT_OK, returnIntent)
                            finish()


                        }


                        else ->  {
                            alertDialog("Invalid Qr Code")
                            finish()
                        }
                    }

                    Toast.makeText(this, "Scan result: ${it.text}", Toast.LENGTH_LONG).show()
                    Log.d(TAG, "text: "+ parsedResult.type)
                }


            }
        }
        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            runOnUiThread {
                Toast.makeText(
                    this, "Camera initialization error: ${it.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }


//        previewView = findViewById(R.id.scanner_view)
//        cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
//        ViewModelProvider(
//            this, ViewModelProvider.AndroidViewModelFactory.getInstance(application)
//        ).get(CameraXViewModel::class.java)
//            .processCameraProvider
//            .observe(
//                this,
//                Observer { provider: ProcessCameraProvider? ->
//                    cameraProvider = provider
//                    if (isCameraPermissionGranted()) {
//                        bindCameraUseCases()
//                    } else {
//                        ActivityCompat.requestPermissions(
//                            this,
//                            arrayOf(Manifest.permission.CAMERA),
//                            PERMISSION_CAMERA_REQUEST
//                        )
//                    }
//                }
//            )
    }

    private fun bindCameraUseCases() {

        codeScanner.startPreview()
//        bindPreviewUseCase()
        //   bindAnalyseUseCase()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }


    override fun onResume() {
        super.onResume()

        if (isCameraPermissionGranted()) {
            codeScanner.startPreview()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                PERMISSION_CAMERA_REQUEST
            )
        }
    }

//    private fun bindPreviewUseCase() {
//        if (cameraProvider == null) {
//            return
//        }
//        if (previewUseCase != null) {
//            cameraProvider!!.unbind(previewUseCase)
//        }
//
//        previewUseCase = Preview.Builder()
////            .setTargetAspectRatio(screenAspectRatio)
//            .setTargetRotation(previewView!!.display.rotation)
//            .build()
//        previewUseCase!!.setSurfaceProvider(previewView!!.surfaceProvider)
//
//        try {
//            cameraProvider!!.bindToLifecycle(
//                /* lifecycleOwner= */this,
//                cameraSelector!!,
//                previewUseCase
//            )
//        } catch (illegalStateException: IllegalStateException) {
//            Log.e(TAG, illegalStateException.message)
//        } catch (illegalArgumentException: IllegalArgumentException) {
//            Log.e(TAG, illegalArgumentException.message)
//        }
//    }

//    private fun bindAnalyseUseCase() {
//        // Note that if you know which format of barcode your app is dealing with, detection will be
//        // faster to specify the supported barcode formats one by one, e.g.
//        val options =  BarcodeScannerOptions.Builder()
//             .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
//             .build()
//        val barcodeScanner: BarcodeScanner = BarcodeScanning.getClient(options)
//
//        if (cameraProvider == null) {
//            return
//        }
//        if (analysisUseCase != null) {
//            cameraProvider!!.unbind(analysisUseCase)
//        }
//
//        analysisUseCase = ImageAnalysis.Builder()
//            .setTargetAspectRatio(screenAspectRatio)
//            .setTargetRotation(previewView!!.display.rotation)
//            .build()
//
//        // Initialize our background executor
//        val cameraExecutor = Executors.newSingleThreadExecutor()
//
//        analysisUseCase?.setAnalyzer(
//            cameraExecutor,
//            ImageAnalysis.Analyzer { imageProxy ->
//                processImageProxy(barcodeScanner, imageProxy)
//            }
//        )
//
//        try {
//            cameraProvider!!.bindToLifecycle(
//                /* lifecycleOwner= */this,
//                cameraSelector!!,
//                analysisUseCase
//            )
//        } catch (illegalStateException: IllegalStateException) {
//            Log.e(TAG, illegalStateException.message)
//        } catch (illegalArgumentException: IllegalArgumentException) {
//            Log.e(TAG, illegalArgumentException.message)
//        }
//    }

//    @SuppressLint("UnsafeExperimentalUsageError")
//    private fun processImageProxy(
//        barcodeScanner: BarcodeScanner,
//        imageProxy: ImageProxy
//    ) {
//        val inputImage =
//            InputImage.fromMediaImage(imageProxy.image!!, imageProxy.imageInfo.rotationDegrees)
//
//        barcodeScanner.process(inputImage)
//            .addOnSuccessListener { barcodes ->
//
//                barcodes?.let {
//                    barcodes.forEach {
//
//                        val bounds: Rect = it.boundingBox
//                        val corners: Array<Point> = it.cornerPoints
//
//                        val rawValue: String = it.rawValue
//
//                        val valueType: Int = it.valueType
//                        // See API reference for complete list of supported types
//                        when (valueType) {
//                            Barcode.FORMAT_QR_CODE -> {
//
//
//                                val ssid: String = it.wifi.ssid
//                                val password: String = it.wifi.password
//                                val type: Int = it.wifi.encryptionType
//                            }
//                            Barcode.FORMAT_CODE_39 -> {
//                                val title: String = it.url.title
//                                val url: String = it.url.url
//                            }
//                        }
//                        Log.d(TAG, it.rawValue)
//                    }
//                }
//
//            }
//            .addOnFailureListener {
//                Log.e(TAG, it.message)
//            }.addOnCompleteListener {
//                // When the image is from CameraX analysis use case, must call image.close() on received
//                // images when finished using them. Otherwise, new images may not be received or the camera
//                // may stall.
//                imageProxy.close()
//            }
//    }

    /**
     *  [androidx.camera.core.ImageAnalysis], [androidx.camera.core.Preview] requires enum value of
     *  [androidx.camera.core.AspectRatio]. Currently it has values of 4:3 & 16:9.
     *
     *  Detecting the most suitable ratio for dimensions provided in @params by counting absolute
     *  of preview ratio to one of the provided values.
     *
     *  @param width - preview width
     *  @param height - preview height
     *  @return suitable aspect ratio
     */
//    private fun aspectRatio(width: Int, height: Int): Int {
//        val previewRatio = max(width, height).toDouble() / min(width, height)
//        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
//            return AspectRatio.RATIO_4_3
//        }
//        return AspectRatio.RATIO_16_9
//    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_CAMERA_REQUEST) {
            if (isCameraPermissionGranted()) {
//                bindCameraUseCases()
                codeScanner.startPreview()
            } else {
                Log.e(TAG, "no camera permission")
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun isCameraPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            baseContext,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private val TAG = QrCodeScanActivity::class.java.simpleName
        private const val PERMISSION_CAMERA_REQUEST = 1
        public const val RESULT_REQUEST_CODE = 666 //NO OF SATAN

        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0
        private const val ARG_CALL_FROM = "callFrom"


        fun start(context: Context,reference: ComplainTabFragment,callFrom:String) {
            val intent = Intent(context, QrCodeScanActivity::class.java)
            intent.putExtra(ARG_CALL_FROM, callFrom)
           // context.startActivity(intent)
            reference.startActivityForResult(intent,RESULT_REQUEST_CODE)
        }
    }
}