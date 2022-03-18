package com.putrash.ocbcmobile.arch.ui.home.scanner

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.View
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.google.common.util.concurrent.ListenableFuture
import com.putrash.common.Constants.BUNDLE_PAYEES
import com.putrash.common.Constants.DEEPLINK_OCBC
import com.putrash.common.EventObserver
import com.putrash.common.setSafeClickListener
import com.putrash.common.toObject
import com.putrash.common.utils.MLKitBarcodeAnalyzer
import com.putrash.common.utils.ScanningResultListener
import com.putrash.data.model.Payees
import com.putrash.ocbcmobile.R
import com.putrash.ocbcmobile.arch.vm.HomeViewModel
import com.putrash.ocbcmobile.base.BaseFragment
import com.putrash.ocbcmobile.databinding.FragmentScannerBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class ScannerFragment : BaseFragment<FragmentScannerBinding, HomeViewModel>(FragmentScannerBinding::inflate), ScanningResultListener {
    override val viewModel: HomeViewModel by sharedViewModel()

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var imageAnalysis: ImageAnalysis
    private var flashEnabled = false

    companion object {
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0
    }

    override fun initView(view: View, savedInstaceState: Bundle?) {
        binding.viewFinder.post {
                cameraExecutor = Executors.newSingleThreadExecutor()
                cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
                cameraProviderFuture.addListener({
                    cameraProvider = cameraProviderFuture.get()
                    bindCameraUseCases(cameraProvider)
                }, ContextCompat.getMainExecutor(requireContext()))

        }
        binding.btnClose.setSafeClickListener {
            findNavController().popBackStack()
        }
    }

    private fun bindCameraUseCases(cameraProvider: ProcessCameraProvider) {
        if (requireActivity().isDestroyed || requireActivity().isFinishing) {
            //This check is to avoid an exception when trying to re-bind use cases but user closes the activity.
            //java.lang.IllegalArgumentException: Trying to create use case mediator with destroyed lifecycle.
            return
        }

        cameraProvider.unbindAll()

        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

        val metrics = DisplayMetrics().also { binding.viewFinder.display?.getRealMetrics(it) }
        val screenAspectRatio = aspectRatio(metrics.widthPixels, metrics.heightPixels)
        val preview = buildPreviewUseCase(screenAspectRatio, binding.viewFinder.display.rotation)

        imageAnalysis =
            buildImageAnalysisUseCase(screenAspectRatio, binding.viewFinder.display.rotation)

        val analyzer = MLKitBarcodeAnalyzer(this)
        imageAnalysis.setAnalyzer(cameraExecutor, analyzer)

        val camera =
            cameraProvider.bindToLifecycle(this, cameraSelector, imageAnalysis, preview)

        preview.setSurfaceProvider(binding.viewFinder.surfaceProvider)

        if (camera.cameraInfo.hasFlashUnit()) {
            binding.btnFlash.setSafeClickListener {
                camera.cameraControl.enableTorch(!flashEnabled)
            }
            camera.cameraInfo.torchState.observe(viewLifecycleOwner) { torchState ->
                if (torchState == TorchState.ON) {
                    flashEnabled = true
                    binding.btnFlash.setImageResource(R.drawable.ic_flash_on)
                } else {
                    flashEnabled = false
                    binding.btnFlash.setImageResource(R.drawable.ic_flash_off)
                }
            }
        } else binding.btnFlash.visibility = View.GONE
    }

    private fun aspectRatio(width: Int, height: Int): Int {
        val previewRatio = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }

    private fun buildPreviewUseCase(aspectRatio: Int, rotation: Int): Preview {
        return Preview.Builder().apply {
            setTargetAspectRatio(aspectRatio)
            setTargetRotation(rotation)
        }.build()
    }

    private fun buildImageAnalysisUseCase(aspectRatio: Int, rotation: Int): ImageAnalysis {
        return ImageAnalysis.Builder().apply {
            setTargetAspectRatio(aspectRatio)
            setTargetRotation(rotation)
        }.build()
    }

    override fun observeLiveData() {
        super.observeLiveData()
        viewModel.prompt.observe(viewLifecycleOwner, EventObserver { show ->
            if (!show) {
                bindCameraUseCases(cameraProvider)
            }
        })
    }

    override fun onScanned(result: String) {
        activity?.runOnUiThread {
            imageAnalysis.clearAnalyzer()
            cameraProvider.unbindAll()

            viewModel.setLoading(true)
            Handler(Looper.getMainLooper()).postDelayed({
                viewModel.setLoading(false)
                if (result.startsWith(DEEPLINK_OCBC)) {
                    try {
                        val json = result.removePrefix(DEEPLINK_OCBC)
                        val data = json.toObject<Payees>()
                        val bundle = bundleOf(BUNDLE_PAYEES to data)
                        viewModel.setPrompt(true)
                        findNavController().navigate(R.id.action_scannerFragment_to_promptFragment, bundle)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        bindCameraUseCases(cameraProvider)
                        showSnackBar(getString(R.string.error_qr_data_not_match))
                    }
                } else {
                    bindCameraUseCases(cameraProvider)
                    showSnackBar(getString(R.string.error_qr_not_match))
                }
            }, 1000)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}