package com.example.artfestproject1

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.artfestproject1.databinding.ActivityShowCameraBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ShowCamera : AppCompatActivity() {
    protected var mMyApp: MyApp? = null
    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var binding: ActivityShowCameraBinding

    var smallImage: String = ""
    var rowAdmin: Int = 0
    var colAdmin: Int = 0
    var fromAdmin: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // app reference
        mMyApp = this.applicationContext as MyApp

        binding = ActivityShowCameraBinding.inflate(layoutInflater)
        val rootview = binding.root
        setContentView(rootview)
        val previousIntent = this.intent
        val count = previousIntent.getIntExtra("countback",0)
        smallImage = previousIntent.getStringExtra("smallImage").toString()
        colAdmin = previousIntent.getIntExtra("colAdmin", 0)
        rowAdmin = previousIntent.getIntExtra("rowAdmin", 0)
        fromAdmin = previousIntent.getBooleanExtra("fromAdmin", false)

        if(allPermissionsGranted())
        {
            startCamera()
        }
        else
        {
            // requestCode 是確保回傳之後拿到的人知道是從哪裡傳回的
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        // set up listener for button
        binding.cameraCaptureButton.setOnClickListener { takePhoto(count) }

        outputDirectory = getimageDirFile(this)

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(allPermissionsGranted())
        {
            startCamera()
        }
        else
        {
           // Toast.makeText(this, "Permissions not granted by user.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun takePhoto(count:Int){
        Log.d("Camera", "Taking Photo.")

        // get imageCapture
        val imageCapture = imageCapture?:return

        // create File to save the image
        val photoFile = File(outputDirectory, SimpleDateFormat(FILENAME_FORMAT, Locale.US
        ).format(System.currentTimeMillis()) + ".jpg")

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // capture pic
        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(this), object: ImageCapture.OnImageSavedCallback{
            override fun onError(exc: ImageCaptureException) {
                Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
            }

            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val savedURI = Uri.fromFile(photoFile)
                val msg = "Photo captured succeed ${savedURI}"
               // Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                Log.d(TAG, msg)
                printImageDir(baseContext)

                // 傳 savedURI 給 edit 頁面
                //try bundle
                val intent_to_edit = Intent(this@ShowCamera, EditActivity::class.java)
                var bundle = Bundle()

                bundle.putString("imageURI", savedURI.toString())
                bundle.putInt("count", count)
                bundle.putString("smallImage", smallImage)
                bundle.putInt("colAdmin", colAdmin)
                bundle.putInt("rowAdmin", rowAdmin)
                bundle.putBoolean("fromAdmin", fromAdmin)
                intent_to_edit.putExtras(bundle)
                startActivity(intent_to_edit)

            }
        })


    }

    private fun startCamera(){

        // get the Listenable Future object of the Camera Process
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {

            // get cameraProvider at the time cameraProvider finishes its computation
            val cameraProvider: ProcessCameraProvider =  cameraProviderFuture.get()

            // set up use case: Preview
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.viewFinder.createSurfaceProvider())
            }

            // set up use case: Capture Image
            imageCapture = ImageCapture.Builder()
                    .build()

            // Select back camera as default
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            // Bind to lifeCycle
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            }catch (exc: Exception)
            {
                Log.e("Camera", "Use case binding failure.", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all{
        ContextCompat.checkSelfPermission(baseContext, it)== PackageManager.PERMISSION_GRANTED
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() } }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraXBasic"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    fun imageDirPath(context: Context): String {
        val Images = File(context.filesDir, "Images")
        return Images.absolutePath
    }

    fun getimageDirFile(context: Context): File {
        val internalEntryPoint = context.filesDir
        if (internalEntryPoint.canWrite()) {
            Log.d("ImageDir", "Can write")
        }
        else
        {
            Log.d("ImageDir", "unable to access")
        }
        // Get into Images
        val imgDir = File(internalEntryPoint.absolutePath, "Images")
        if (!imgDir.exists()) {
            imgDir.mkdir()
        }
        return imgDir
    }

    fun printImageDir(context: Context) {
        val imageDirpath = imageDirPath(context)
        val imageDirFile = File(imageDirpath)
        for (f in imageDirFile.list()) {
            println(f)
            Log.d("ImageDir", f!!)
        }
    }

    override fun onResume(){
        super.onResume()
        // app reference relink
        mMyApp!!.setCurrentActivity(this);
    }
}