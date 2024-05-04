package com.example.workconnect.ui.attendance

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.example.workconnect.R
import com.example.workconnect.databinding.ActivityCheckInBinding
import com.example.workconnect.utils.Constants.Companion.CHECK_IN
import com.example.workconnect.utils.Constants.Companion.CHECK_OUT
import com.example.workconnect.utils.Constants.Companion.OPERATION_TYPE
import com.example.workconnect.utils.UiState
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class AttendanceActivity : AppCompatActivity() {

    lateinit var binding: ActivityCheckInBinding

    val viewModel: AttendanceViewModel by viewModels()

    var operationType = ""

    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions())
        { permissions ->
            // Handle Permission granted/rejected
            var permissionGranted = true
            permissions.entries.forEach {
                if (it.key in REQUIRED_PERMISSIONS && !it.value)
                    permissionGranted = false
            }
            if (!permissionGranted) {
                Toast.makeText(baseContext,
                    "Permission request denied",
                    Toast.LENGTH_SHORT).show()
            } else {
                startCamera()
            }
        }
    private var imageCapture: ImageCapture? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCheckInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions()
        }
        if (operationType == CHECK_IN){
            binding.checkInButton.text = "CheckIn"
        }
        if (operationType == CHECK_OUT){
            binding.checkInButton.text = "CheckOut"
        }

        binding.checkInButton.setOnClickListener {
            takePhoto()
        }

        initObservers()


    }

    private fun initObservers() {
        viewModel.uiState.observe(this) {
            handleUiState(it)
        }
    }

    private fun handleUiState(state: UiState) {
        when(state){
            UiState.SUCCESS -> {
                val message = if (operationType == CHECK_IN){"Check-in successful"} else{"Check-out successful"}
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                binding.successAnim.visibility = View.VISIBLE
                binding.viewFinder.isVisible = false
                binding.scannerAnim.visibility = View.GONE
            }
            UiState.LOADING -> {
                binding.scannerAnim.visibility = View.VISIBLE
                binding.viewFinder.isVisible = false
            }
            UiState.ERROR -> {
                val message = if (operationType == CHECK_IN){"Check-in failed"} else{"Check-out failed"}
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                binding.viewFinder.isVisible = true
                binding.scannerAnim.visibility = View.GONE
            }
        }
    }

    private fun requestPermissions() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }


    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    val viewFinder = binding.viewFinder
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture)

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time stamped name and MediaStore entry.
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
            .build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = output.savedUri ?: return // Handle null Uri

                    val file = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                        // For versions below Android 10 (API level 29)
                        File(savedUri.path ?: return)
                    } else {
                        // For Android 10 (API level 29) and higher
                        val inputStream = contentResolver.openInputStream(savedUri) ?: return
                        val outputFile = File(filesDir, "captured_image.jpg") // You can change the file name as needed
                        FileOutputStream(outputFile).use { outputStream ->
                            inputStream.copyTo(outputStream)
                        }
                        outputFile
                    }

                    val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                    val outputStream = FileOutputStream(file)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream) // Adjust quality (1-100)
                    outputStream.close()

                    operationType  = intent.getStringExtra(OPERATION_TYPE).toString()
                    Log.d("operationType",operationType)
                    // Upload the file
                    if (operationType == CHECK_IN){
                        viewModel.checkInWithFace(file)
                    }
                    if (operationType == CHECK_OUT){
                        viewModel.checkOutWithFace(file)
                    }


                    // Now you have the File object representing the captured image
                    // You can use it as needed
                    Log.d(TAG, "Photo capture succeeded: ${file.absolutePath}")
                }
            }
        )
    }

    companion object {
        private const val TAG = "CheckIn Activity"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }
}