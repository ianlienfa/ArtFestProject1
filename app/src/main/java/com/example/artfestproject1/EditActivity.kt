package com.example.artfestproject1

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.print.PrintManager
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.print.PrintHelper
import com.example.artfestproject1.MyImage.ImageGallery
import com.example.artfestproject1.databinding.ActivityEditBinding
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.default
import id.zelory.compressor.constraint.destination
import id.zelory.compressor.constraint.resolution
import kotlinx.coroutines.*
import org.bytedeco.opencv.opencv_core.Mat
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Runnable
import kotlin.system.exitProcess


class EditActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // file parameters
        var newFilename: String = ""
        val baseImgName = "[1][7].jpg"

        // Image parameters, htc: 3024 * 4032
        val crop_x = 32
        val crop_y = 300
        val CROP_WIDTH = 2500
        val CROP_HEIGHT = 2500
        val expected_pixel_w = 108
        val expected_pixel_h = 108

        // View binding
        val binding = ActivityEditBinding.inflate(layoutInflater)
        val rootview = binding.root
        val imageView = binding.imageView
        val loadingPanel = binding.loadingPanel
        val printButton = binding.printButton
        val compute_button = binding.computeButton
        val user_send_print_button = binding.userSendPrintButton

        // Set Content view
        setContentView(rootview)

        // 取得傳遞過來的資料
        val previousIntent = this.intent
        val imageURI = previousIntent.getStringExtra("imageURI")
        val uri: Uri = Uri.parse(imageURI)

        // get filename from URI
        var filename: String = uri.path.toString()
        val cut: Int = filename.lastIndexOf('/')
        if (cut != -1) {
            filename = filename.substring(cut + 1)
        }

        lifecycle.coroutineScope.launch {
            // Pretreatment -- Crop to square
            newFilename = doPhotoCrop(filename, baseContext, crop_x, crop_y, CROP_WIDTH, CROP_HEIGHT)

            // Pretreatment -- Compress

            runBlocking {
                newFilename = doPhotoCompress(newFilename, baseContext, expected_pixel_w, expected_pixel_h)
                Log.d("Thread", "1")
            }

            // Pretreatment -- Compress might leave some pixels, do some pruning
            Log.d("Thread", "2")
            newFilename = doPhotoCrop(newFilename, baseContext, 0, 0, expected_pixel_w, expected_pixel_h)

            // Prepare for the image show
            val imgFile = File(ImageGallery.imageDirFile(baseContext), newFilename)
            val imgFilePath = imgFile.absolutePath
            val newURI = Uri.parse(imgFilePath)

            // only update UI on UI thread, hide the loadingPanel and show the others
            binding.loadingPanel.post {
                binding.loadingPanel.visibility = GONE
            }
            binding.printButton.post {
                binding.printButton.visibility = VISIBLE
            }
            binding.computeButton
            binding.computeButton.post {
                binding.computeButton.visibility = VISIBLE
            }
            binding.imageView.post {
                binding.imageView.setImageURI(newURI)
                binding.imageView.visibility = VISIBLE
            }

        }

        compute_button.setOnClickListener{
            Thread(Runnable {

                // Load user photo
                val img_user = ImageGallery.stdLoadImg(newFilename, this)

                // put image for test
                ImageGallery.DIRPATH = ImageGallery.imageDirPath(this)+'/'

                // do algorithm
                System.out.println("${ImageGallery.DIRPATH}")
                val image = ImageGallery.stdLoadImg("rex.jpg", this)
                val imageGallery = ImageGallery(image, 108, 108)
                val img_base = ImageGallery.stdLoadImg(baseImgName, this)
                val img_new = imageGallery.algorithm_BAI(img_user, img_base)
                ImageGallery.printImageDir(this)    // For Debug

                // end testing for algorithm ----

                // Write computed img
                newFilename = "cmp_"+newFilename
                ImageGallery.internalImgWrite(newFilename, img_new,this)
                //TODO:
                Log.d("Admin", imageGallery.get_Imgstatus(0, 0).toString())

                // Prepare for the image show
                val internalEntryPoint: File = this.getFilesDir()
                val imgDir = File(internalEntryPoint.absolutePath, "Images")
                val imgFile: File = File(imgDir, newFilename)
                val imgFilePath = imgFile.absolutePath
                val newURI = Uri.parse(imgFilePath)

                // only update UI on UI thread, hide the loadingPanel and show the others
                loadingPanel.post{
                    loadingPanel.visibility = GONE
                }
                printButton.post{
                    printButton.visibility = VISIBLE
                }
                compute_button.post{
                    compute_button.visibility = VISIBLE
                }
                imageView.post{
                    imageView.setImageURI(newURI)
                    imageView.visibility = VISIBLE
                }

            }).start()
        }

        // Print Button
        printButton.setOnClickListener{
            if(!newFilename.equals("")) {
                val imgFile = File(ImageGallery.imageDirFile(this), newFilename)
                val imgFilePath = imgFile.absolutePath
                if(File(imgFilePath).exists()) {
                    doPhotoPrint(imgFilePath)
                }
            }
        }

        // Compute & Print
        user_send_print_button.setOnClickListener {

            Thread(Runnable {

                // 轉換照片並存檔
                newFilename = "algorithm_"+filename
                var img_user: Mat = ImageGallery.internalImgRead(filename, this)
                img_user = ImageGallery.matCrop(img_user, crop_x, crop_y, CROP_WIDTH, CROP_WIDTH)

                // start testing for algorithm ----

                // put image for test
                ImageGallery.DIRPATH = ImageGallery.imageDirPath(this)+'/'

                // do algorithm
                System.out.println("${ImageGallery.DIRPATH}")
                val image = ImageGallery.stdLoadImg("rex.jpg", this)
                val imageGallery = ImageGallery(image, 108, 108)
                val img_base = ImageGallery.stdLoadImg(baseImgName, this)
                val img_new = imageGallery.algorithm_BAI(img_user, img_base)
                ImageGallery.printImageDir(this)    // For Debug

                // end testing for algorithm ----

                // Write computed img
                ImageGallery.internalImgWrite(newFilename, img_new,this)
                //TODO:
                Log.d("Admin", imageGallery.get_Imgstatus(0, 0).toString())

                // Prepare for the image show
                val internalEntryPoint: File = this.getFilesDir()
                val imgDir = File(internalEntryPoint.absolutePath, "Images")
                val imgFile: File = File(imgDir, newFilename)
                val imgFilePath = imgFile.absolutePath
                val newURI = Uri.parse(imgFilePath)

                // only update UI on UI thread, hide the loadingPanel and show the others
                loadingPanel.post{
                    loadingPanel.visibility = GONE
                }
                printButton.post{
                    printButton.visibility = VISIBLE
                }
                compute_button.post{
                    compute_button.visibility = VISIBLE
                }
                imageView.post{
                    imageView.setImageURI(newURI)
                    imageView.visibility = VISIBLE
                }

                if(!newFilename.equals("")) {
                    val imgFile = File(ImageGallery.imageDirFile(this), newFilename)
                    val imgFilePath = imgFile.absolutePath
                    if(File(imgFilePath).exists()) {
                        doPhotoPrint(imgFilePath)
                    }
                }

            }).start()

        }
    }
//
//    private fun photoPretreatmentAndShow(binding: ActivityEditBinding, filename: String, w: Int, h: Int) {
//        lifecycleScope.launch {
//            // Crop Photo
//            newFilename = "cp_"+filename
//            var img_user: Mat = ImageGallery.internalImgRead(filename, baseContext)
//            img_user = ImageGallery.matCrop(img_user, crop_x, crop_y, CROP_WIDTH, CROP_WIDTH)
//            ImageGallery.internalImgWrite(newFilename, img_user,baseContext)
//            Log.d("name", newFilename)
//
//            // Compress photo
//            val internalEntryPoint = baseContext.filesDir
//            if(internalEntryPoint.canRead()){
//                // Get into Images
//                val imgDir = File(internalEntryPoint.absolutePath, "Images")
//                if (!imgDir.exists()) {
//                    Log.d("Write", "ImageGallery.internalWrite: Unable to write into internal storage.")
//                }
//                // Write Img
//                val imgfile = File(imgDir, filename)
//                val desFile = File(imgDir, "cmpr_"+filename)
//                newFilename = "cmpr_"+filename
//
//                // Compress and save
//                val compressedImageFile = Compressor.compress(baseContext, imgfile){
//                    resolution(w, h)
//                    destination(desFile)
//                }
//            }
//
//            // Compress might leave some pixels, do some pruning
//            img_user = ImageGallery.internalImgRead(newFilename, baseContext)
//            img_user = ImageGallery.matCrop(img_user, crop_x, crop_y, w, h)
//            ImageGallery.internalImgWrite(newFilename, img_user,baseContext)
//
//            Log.d("name", newFilename)
//            // Prepare for the image show
//            val imgFile = File(ImageGallery.imageDirFile(baseContext), newFilename)
//            val imgFilePath = imgFile.absolutePath
//            val newURI = Uri.parse(imgFilePath)
//
//            // only update UI on UI thread, hide the loadingPanel and show the others
//            binding.loadingPanel.post{
//                binding.loadingPanel.visibility = GONE
//            }
//            binding.printButton.post{
//                binding.printButton.visibility = VISIBLE
//            }
//            binding.computeButton
//            binding.computeButton.post{
//                binding.computeButton.visibility = VISIBLE
//            }
//            binding.imageView.post{
//                binding.imageView.setImageURI(newURI)
//                binding.imageView.visibility = VISIBLE
//            }
//        }
//    }
//
//

    private fun doPhotoCrop(filename: String, context: Context, crop_x: Int, crop_y: Int, CROP_WIDTH: Int, CROP_HEIGHT: Int): String{
        // Crop Photo
        var newFilename = "cp_"+filename
        while(!ImageGallery.getImageDir(context).canExecute())
        {
            Log.d("IO", "not yet")
        }
        if(ImageGallery.getImageDir(context).canExecute()) {
            var img_user: Mat = ImageGallery.internalImgRead(filename, context)
            if(img_user.cols() != CROP_WIDTH || img_user.rows() != CROP_HEIGHT) {
                img_user = ImageGallery.matCrop(img_user, crop_x, crop_y, CROP_WIDTH, CROP_HEIGHT)
            }
            ImageGallery.internalImgWrite(newFilename, img_user, context)
            Log.e("name", newFilename)
        }
        else{
            Log.d("File", filename+"Not ready yet")
            System.exit(1)
        }
        return newFilename
    }

    private suspend fun doPhotoCompress(filename: String, context: Context, w: Int, h: Int): String{
        val internalEntryPoint = context.filesDir
        var newFilename = ""
        if(internalEntryPoint.canRead()){
            // Get into Images
            val imgDir = File(internalEntryPoint.absolutePath, "Images")
            if (!imgDir.exists()) {
                Log.d("Write", "ImageGallery.internalWrite: Unable to write into internal storage.")
            }
            // Write Img
            val imgfile = File(imgDir, filename)
            val desFile = File(imgDir, "cmpr_"+filename)
            newFilename = "cmpr_"+filename

            // Compress and save
            val compressedImageFile = Compressor.compress(context, imgfile) {
                resolution(w, h)
                destination(desFile)
            }

        }
        return newFilename
    }


    private fun doPhotoPrint(filepath: String) {
        this.also { context ->
            PrintHelper(context).apply {
                scaleMode = PrintHelper.SCALE_MODE_FIT
            }.also { printHelper ->
                val bitmap = BitmapFactory.decodeFile(filepath)
                printHelper.colorMode = PrintHelper.COLOR_MODE_MONOCHROME
                printHelper.printBitmap("droids.jpg - test print", bitmap, PrintHelper.OnPrintFinishCallback {
                    Log.d("Print", "print finished.")
                })
            }
        }
        val printManager = baseContext.getSystemService(Context.PRINT_SERVICE) as PrintManager
        val printJobs = printManager.printJobs
        for(job in printJobs)
        {
            if(job.equals("droids.jpg - test print"))
            {
                if(job.isCompleted()){
                    Toast.makeText(getApplicationContext(), "print_complete", Toast.LENGTH_LONG).show();
                    Log.d("Print", "print_complete.")

                }
                else if(job.isFailed()){
                    Toast.makeText(getApplicationContext(), "print_failed", Toast.LENGTH_LONG).show();
                    Log.d("Print", "print_failed`b .")
                }
                else{
                    Log.d("Print", "else.")
                }
            }
        }
    }
}