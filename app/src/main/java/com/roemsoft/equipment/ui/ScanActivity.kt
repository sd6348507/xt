package com.roemsoft.equipment.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.permissionx.guolindev.PermissionX
import com.roemsoft.common.unit.PermissionUtils
import com.roemsoft.equipment.R
import timber.log.Timber
import kotlin.math.abs

class ScanActivity : AppCompatActivity() {

    companion object {
        const val SCAN_RESULT = "scan:result"
        const val SCAN_SUCCESS = 0
        const val SCAN_FAILURE = 1
    }

    private lateinit var previewView: PreviewView
    private lateinit var scanLine: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)

        previewView = findViewById(R.id.preview)
        scanLine = findViewById(R.id.capture_scan_line)

        val animation = TranslateAnimation(
            Animation.RELATIVE_TO_PARENT,
            0.0f,
            Animation.RELATIVE_TO_PARENT,
            0.0f,
            Animation.RELATIVE_TO_PARENT,
            0.0f,
            Animation.RELATIVE_TO_PARENT,
            0.9f
        )
        animation.duration = 4500
        animation.repeatCount = -1
        animation.repeatMode = Animation.RESTART
        scanLine.startAnimation(animation)

        requestCamera()

        previewView.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                startCamera()
                previewView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }

        })

    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener( {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .setTargetAspectRatio(aspectRatio())
                .setTargetRotation(previewView.display.rotation)
                .build()
                .also { it.setSurfaceProvider(previewView.surfaceProvider) }

            // 创建BarcodeScanner实例
            val barcodeScanner: BarcodeScanner = BarcodeScanning.getClient(
                BarcodeScannerOptions.Builder()
                    .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                    .build()
            )

            // 创建ImageAnalysis实例
            val imageAnalysis = ImageAnalysis.Builder()
                .setTargetAspectRatio(aspectRatio())
                .setTargetRotation(previewView.display.rotation)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(ContextCompat.getMainExecutor(this)) { image ->
                        // 获取ImageProxy对象
                        val mediaImage = image.image

                        if (mediaImage != null) {
                            val inputImage = InputImage.fromMediaImage(
                                mediaImage,
                                image.imageInfo.rotationDegrees
                            )

                            // 使用BarcodeScanner识别二维码
                            barcodeScanner.process(inputImage)
                                .addOnSuccessListener { barcodes ->
                                    for (barcode in barcodes) {
                                        if (barcode.format == Barcode.FORMAT_QR_CODE) {
                                            val value = barcode.rawValue
                                            onScannerResult(value)
                                            // 在UI线程中更新TextView
                                            //     runOnUiThread { onScannerResult(value ?: "") }
                                        }
                                    }
                                }
                                .addOnFailureListener {
                                    //    runOnUiThread { onScannerFailure() }
                                    onScannerFailure()
                                }
                                .addOnCompleteListener { image.close() }
                        } else {
                            image.close()
                        }
                    }
                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // 绑定ImageAnalysis实例到相机生命周期中
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis)
            } catch(exc: Exception) {
                Timber.tag("ScanActivity").e("====> 初始化相机失败 $exc")
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun requestCamera() {
        PermissionX.init(this)
            .permissions(Manifest.permission.CAMERA)
            .request { allGranted, _, deniedList ->
                if (allGranted) {
                    Toast.makeText(this, "同意摄像机权限", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "申请摄像机权限被拒绝：$deniedList", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun onScannerResult(result: String?) {
        setResult(SCAN_SUCCESS, Intent().putExtra(SCAN_RESULT, result?.trim()))
        finish()
    }

    private fun onScannerFailure() {
        Timber.tag("ScanActivity").i("====> 扫描失败")
        setResult(SCAN_FAILURE)
        finish()
    }

    /**
     *     RATIO_4_3_VALUE = 4.0 / 3.0;
     *     RATIO_16_9_VALUE = 16.0 / 9.0;
     */
    private fun aspectRatio(): Int {
        val width = previewView.width
        val height = previewView.height
        val previewRatio = width.coerceAtLeast(height).toDouble() / width.coerceAtMost(height).toDouble()
        return if (abs(previewRatio - 4.0 / 3.0) <= abs(previewRatio - 16.0 / 9.0)) {
            AspectRatio.RATIO_4_3
        } else AspectRatio.RATIO_16_9
    }
}